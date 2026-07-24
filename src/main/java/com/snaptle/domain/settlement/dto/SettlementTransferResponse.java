package com.snaptle.domain.settlement.dto;

import com.snaptle.domain.settlement.SettlementTransfer;
import java.math.BigDecimal;

public record SettlementTransferResponse(Long fromUserId, Long toUserId, BigDecimal amount) {

    public static SettlementTransferResponse from(SettlementTransfer transfer) {
        return new SettlementTransferResponse(transfer.getFromUserId(), transfer.getToUserId(), transfer.getAmount());
    }
}
