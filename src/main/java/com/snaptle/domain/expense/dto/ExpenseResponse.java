package com.snaptle.domain.expense.dto;

import com.snaptle.domain.expense.Expense;
import com.snaptle.domain.expense.ExpenseCategory;
import com.snaptle.domain.expense.ExpenseParticipant;
import com.snaptle.global.common.Currency;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ExpenseResponse(
        Long id,
        Long tripId,
        Long payerId,
        String merchantName,
        LocalDateTime paidAt,
        Currency currency,
        BigDecimal originalAmount,
        BigDecimal exchangeRate,
        BigDecimal convertedAmount,
        String receiptImageUrl,
        ExpenseCategory category,
        List<ExpenseParticipantResponse> participants
) {

    public static ExpenseResponse of(Expense expense, List<ExpenseParticipant> participants) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getTripId(),
                expense.getPayerId(),
                expense.getMerchantName(),
                expense.getPaidAt(),
                expense.getCurrency(),
                expense.getOriginalAmount(),
                expense.getExchangeRate(),
                expense.getConvertedAmount(),
                expense.getReceiptImageUrl(),
                expense.getCategory(),
                participants.stream().map(ExpenseParticipantResponse::from).toList()
        );
    }
}
