package com.snaptle.domain.expense.dto;

import com.snaptle.domain.expense.ExpenseParticipant;
import java.math.BigDecimal;

public record ExpenseParticipantResponse(Long userId, BigDecimal shareAmount) {

    public static ExpenseParticipantResponse from(ExpenseParticipant participant) {
        return new ExpenseParticipantResponse(participant.getUserId(), participant.getShareAmount());
    }
}
