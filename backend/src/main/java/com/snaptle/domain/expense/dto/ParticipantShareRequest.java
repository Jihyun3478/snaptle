package com.snaptle.domain.expense.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record ParticipantShareRequest(
        @NotNull Long userId,
        @Positive BigDecimal shareAmount
) {
}
