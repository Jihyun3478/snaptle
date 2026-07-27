package com.snaptle.domain.settlement.dto;

import com.snaptle.domain.settlement.Settlement;
import java.time.LocalDateTime;
import java.util.List;

public record SettlementResponse(
        Long id,
        Long tripId,
        LocalDateTime calculatedAt,
        List<MemberBalanceResponse> balances,
        List<SettlementTransferResponse> transfers
) {

    public static SettlementResponse of(Settlement settlement, List<MemberBalanceResponse> balances,
                                         List<SettlementTransferResponse> transfers) {
        return new SettlementResponse(
                settlement.getId(), settlement.getTripId(), settlement.getCreatedAt(), balances, transfers
        );
    }
}
