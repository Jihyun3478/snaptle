package com.snaptle.domain.personaldebt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CreatePersonalDebtRequest(
        @NotNull Long creditorId,
        @NotNull Long debtorId,
        @NotNull @Positive BigDecimal amount,
        @NotBlank @Size(max = 255) String reason
) {
}
