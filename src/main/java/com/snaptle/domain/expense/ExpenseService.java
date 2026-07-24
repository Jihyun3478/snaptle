package com.snaptle.domain.expense;

import com.snaptle.domain.expense.dto.CreateExpenseRequest;
import com.snaptle.domain.expense.dto.ExpenseResponse;
import com.snaptle.domain.expense.dto.OcrRecognizeResponse;
import com.snaptle.domain.trip.Trip;
import com.snaptle.domain.trip.TripService;
import com.snaptle.global.common.Currency;
import com.snaptle.global.exception.ErrorCode;
import com.snaptle.global.exception.SnaptleException;
import com.snaptle.global.exchangerate.ExchangeRateClient;
import com.snaptle.global.ocr.ReceiptOcrClient;
import com.snaptle.global.ocr.ReceiptOcrResult;
import com.snaptle.global.storage.FileStorageService;
import com.snaptle.global.storage.StoredFile;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final TripService tripService;
    private final FileStorageService fileStorageService;
    private final ReceiptOcrClient receiptOcrClient;
    private final ExchangeRateClient exchangeRateClient;

    public ExpenseService(ExpenseRepository expenseRepository, TripService tripService,
                           FileStorageService fileStorageService, ReceiptOcrClient receiptOcrClient,
                           ExchangeRateClient exchangeRateClient) {
        this.expenseRepository = expenseRepository;
        this.tripService = tripService;
        this.fileStorageService = fileStorageService;
        this.receiptOcrClient = receiptOcrClient;
        this.exchangeRateClient = exchangeRateClient;
    }

    public OcrRecognizeResponse recognizeReceipt(Long userId, Long tripId, MultipartFile file) {
        tripService.requireMember(tripId, userId);

        StoredFile storedFile = fileStorageService.store(file);
        Optional<ReceiptOcrResult> result = receiptOcrClient.recognize(storedFile.content(), storedFile.contentType());

        return result
                .map(r -> OcrRecognizeResponse.recognized(storedFile.url(), r))
                .orElseGet(() -> OcrRecognizeResponse.notRecognized(storedFile.url()));
    }

    @Transactional
    public ExpenseResponse createExpense(Long userId, Long tripId, CreateExpenseRequest request) {
        Trip trip = tripService.getTripOrThrow(tripId);
        tripService.requireMember(tripId, userId);

        if (!tripService.isMember(tripId, request.payerId())) {
            throw new SnaptleException(ErrorCode.PAYER_NOT_TRIP_MEMBER);
        }

        BigDecimal exchangeRate = resolveExchangeRate(request.currency(), trip.getBaseCurrency(),
                request.manualExchangeRate());
        BigDecimal convertedAmount = request.amount().multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);

        Expense expense = Expense.create(
                tripId, request.payerId(), request.merchantName(), request.paidAt(), request.currency(),
                request.amount(), exchangeRate, convertedAmount, request.receiptImageUrl(), request.category()
        );

        return ExpenseResponse.from(expenseRepository.save(expense));
    }

    public List<ExpenseResponse> getExpenses(Long userId, Long tripId, ExpenseCategory category) {
        tripService.requireMember(tripId, userId);

        List<Expense> expenses = category == null
                ? expenseRepository.findAllByTripIdOrderByPaidAtDesc(tripId)
                : expenseRepository.findAllByTripIdAndCategoryOrderByPaidAtDesc(tripId, category);

        return expenses.stream().map(ExpenseResponse::from).toList();
    }

    private BigDecimal resolveExchangeRate(Currency from, Currency to, BigDecimal manualExchangeRate) {
        if (from == to) {
            return BigDecimal.ONE;
        }

        return exchangeRateClient.getRate(from, to)
                .or(() -> Optional.ofNullable(manualExchangeRate)
                        .filter(rate -> rate.compareTo(BigDecimal.ZERO) > 0))
                .orElseThrow(() -> new SnaptleException(ErrorCode.EXCHANGE_RATE_UNAVAILABLE));
    }
}
