package com.snaptle.domain.expense;

import com.snaptle.domain.expense.dto.CreateExpenseRequest;
import com.snaptle.domain.expense.dto.ExpenseResponse;
import com.snaptle.domain.expense.dto.OcrRecognizeResponse;
import com.snaptle.domain.expense.dto.ParticipantShareRequest;
import com.snaptle.domain.trip.Trip;
import com.snaptle.domain.trip.TripService;
import com.snaptle.global.common.Currency;
import com.snaptle.global.exception.ErrorCode;
import com.snaptle.global.exception.SnaptleException;
import com.snaptle.global.exchangerate.ExchangeRateClient;
import com.snaptle.global.ocr.ReceiptOcrClient;
import com.snaptle.global.ocr.ReceiptOcrResult;
import com.snaptle.global.storage.FileStorageProperties;
import com.snaptle.global.storage.FileStorageService;
import com.snaptle.global.storage.StoredFile;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
public class ExpenseService {

    private static final BigDecimal MAX_MANUAL_EXCHANGE_RATE = BigDecimal.valueOf(100_000);
    private static final BigDecimal CENT = new BigDecimal("0.01");

    private final ExpenseRepository expenseRepository;
    private final ExpenseParticipantRepository expenseParticipantRepository;
    private final TripService tripService;
    private final FileStorageService fileStorageService;
    private final ReceiptOcrClient receiptOcrClient;
    private final ExchangeRateClient exchangeRateClient;
    private final FileStorageProperties fileStorageProperties;

    public ExpenseService(ExpenseRepository expenseRepository,
                           ExpenseParticipantRepository expenseParticipantRepository, TripService tripService,
                           FileStorageService fileStorageService, ReceiptOcrClient receiptOcrClient,
                           ExchangeRateClient exchangeRateClient, FileStorageProperties fileStorageProperties) {
        this.expenseRepository = expenseRepository;
        this.expenseParticipantRepository = expenseParticipantRepository;
        this.tripService = tripService;
        this.fileStorageService = fileStorageService;
        this.receiptOcrClient = receiptOcrClient;
        this.exchangeRateClient = exchangeRateClient;
        this.fileStorageProperties = fileStorageProperties;
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
        validateReceiptImageUrl(request.receiptImageUrl());

        BigDecimal exchangeRate = resolveExchangeRate(request.currency(), trip.getBaseCurrency(),
                request.manualExchangeRate());
        BigDecimal convertedAmount = request.amount().multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);

        Expense expense = expenseRepository.save(Expense.create(
                tripId, request.payerId(), request.merchantName(), request.paidAt(), request.currency(),
                request.amount(), exchangeRate, convertedAmount, request.receiptImageUrl(), request.category()
        ));

        List<ExpenseParticipant> participants = buildParticipants(tripId, expense.getId(), convertedAmount,
                request.participants());
        expenseParticipantRepository.saveAll(participants);

        return ExpenseResponse.of(expense, participants);
    }

    public List<ExpenseResponse> getExpenses(Long userId, Long tripId, ExpenseCategory category) {
        tripService.requireMember(tripId, userId);

        List<Expense> expenses = category == null
                ? expenseRepository.findAllByTripIdOrderByPaidAtDesc(tripId)
                : expenseRepository.findAllByTripIdAndCategoryOrderByPaidAtDesc(tripId, category);

        List<Long> expenseIds = expenses.stream().map(Expense::getId).toList();
        Map<Long, List<ExpenseParticipant>> participantsByExpenseId = expenseParticipantRepository
                .findAllByExpenseIdIn(expenseIds).stream()
                .collect(Collectors.groupingBy(ExpenseParticipant::getExpenseId));

        return expenses.stream()
                .map(expense -> ExpenseResponse.of(expense,
                        participantsByExpenseId.getOrDefault(expense.getId(), List.of())))
                .toList();
    }

    private List<ExpenseParticipant> buildParticipants(Long tripId, Long expenseId, BigDecimal convertedAmount,
                                                         List<ParticipantShareRequest> participantRequests) {
        if (participantRequests.isEmpty()) {
            throw new SnaptleException(ErrorCode.EMPTY_PARTICIPANTS);
        }

        Set<Long> userIds = new HashSet<>();
        for (ParticipantShareRequest participant : participantRequests) {
            if (!userIds.add(participant.userId())) {
                throw new SnaptleException(ErrorCode.DUPLICATE_PARTICIPANT);
            }
        }

        Set<Long> memberUserIds = tripService.memberUserIdsAmong(tripId, userIds);
        if (!memberUserIds.containsAll(userIds)) {
            throw new SnaptleException(ErrorCode.PARTICIPANT_NOT_TRIP_MEMBER);
        }

        long specifiedCount = participantRequests.stream().filter(p -> p.shareAmount() != null).count();
        if (specifiedCount == participantRequests.size()) {
            return buildCustomSplit(expenseId, convertedAmount, participantRequests);
        }
        if (specifiedCount == 0) {
            return buildEqualSplit(expenseId, convertedAmount, participantRequests);
        }
        throw new SnaptleException(ErrorCode.INVALID_PARTICIPANT_SPLIT);
    }

    private List<ExpenseParticipant> buildCustomSplit(Long expenseId, BigDecimal convertedAmount,
                                                        List<ParticipantShareRequest> participantRequests) {
        List<BigDecimal> roundedShares = participantRequests.stream()
                .map(p -> p.shareAmount().setScale(2, RoundingMode.HALF_UP))
                .toList();

        BigDecimal sum = roundedShares.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        if (sum.compareTo(convertedAmount) != 0 || roundedShares.stream().anyMatch(s -> s.compareTo(BigDecimal.ZERO) <= 0)) {
            throw new SnaptleException(ErrorCode.INVALID_PARTICIPANT_SPLIT);
        }

        List<ExpenseParticipant> participants = new java.util.ArrayList<>(participantRequests.size());
        for (int i = 0; i < participantRequests.size(); i++) {
            participants.add(ExpenseParticipant.of(expenseId, participantRequests.get(i).userId(), roundedShares.get(i)));
        }
        return participants;
    }

    private List<ExpenseParticipant> buildEqualSplit(Long expenseId, BigDecimal convertedAmount,
                                                       List<ParticipantShareRequest> participantRequests) {
        int n = participantRequests.size();
        BigDecimal base = convertedAmount.divide(BigDecimal.valueOf(n), 2, RoundingMode.DOWN);
        BigDecimal remainder = convertedAmount.subtract(base.multiply(BigDecimal.valueOf(n)));
        int remainderUnits = remainder.divide(CENT, 0, RoundingMode.HALF_UP).intValueExact();

        List<ExpenseParticipant> participants = new java.util.ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            BigDecimal share = i < remainderUnits ? base.add(CENT) : base;
            participants.add(ExpenseParticipant.of(expenseId, participantRequests.get(i).userId(), share));
        }
        return participants;
    }

    private BigDecimal resolveExchangeRate(Currency from, Currency to, BigDecimal manualExchangeRate) {
        if (from == to) {
            return BigDecimal.ONE;
        }

        Optional<BigDecimal> rate = exchangeRateClient.getRate(from, to);
        if (rate.isPresent()) {
            return rate.get();
        }

        if (manualExchangeRate == null) {
            throw new SnaptleException(ErrorCode.EXCHANGE_RATE_UNAVAILABLE);
        }
        if (manualExchangeRate.compareTo(BigDecimal.ZERO) <= 0
                || manualExchangeRate.compareTo(MAX_MANUAL_EXCHANGE_RATE) > 0) {
            throw new SnaptleException(ErrorCode.INVALID_EXCHANGE_RATE);
        }
        return manualExchangeRate;
    }

    private void validateReceiptImageUrl(String receiptImageUrl) {
        if (receiptImageUrl == null) {
            return;
        }
        if (!receiptImageUrl.startsWith(fileStorageProperties.publicBaseUrl())) {
            throw new SnaptleException(ErrorCode.INVALID_RECEIPT_IMAGE_URL);
        }
    }
}
