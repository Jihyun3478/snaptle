package com.snaptle.domain.expense.dto;

import com.snaptle.domain.expense.Expense;
import com.snaptle.domain.expense.ExpenseCategory;
import com.snaptle.global.common.Currency;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
        ExpenseCategory category
) {

    public static ExpenseResponse from(Expense expense) {
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
                expense.getCategory()
        );
    }
}
