package com.snaptle.domain.personaldebt.dto;

import com.snaptle.domain.personaldebt.PersonalDebt;
import java.math.BigDecimal;

public record PersonalDebtResponse(
        Long id,
        Long tripId,
        Long creditorId,
        Long debtorId,
        BigDecimal amount,
        String reason
) {

    public static PersonalDebtResponse from(PersonalDebt debt) {
        return new PersonalDebtResponse(
                debt.getId(), debt.getTripId(), debt.getCreditorId(), debt.getDebtorId(),
                debt.getAmount(), debt.getReason()
        );
    }
}
