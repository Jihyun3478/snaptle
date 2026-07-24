package com.snaptle.domain.expense.dto;

import com.snaptle.domain.expense.ExpenseCategory;
import com.snaptle.global.common.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateExpenseRequest(
        @NotNull Long payerId,
        @NotBlank String merchantName,
        @NotNull @Positive BigDecimal amount,
        @NotNull Currency currency,
        @NotNull LocalDateTime paidAt,
        @NotNull ExpenseCategory category,
        String receiptImageUrl,
        BigDecimal manualExchangeRate
) {
}
