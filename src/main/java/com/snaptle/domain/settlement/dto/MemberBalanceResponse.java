package com.snaptle.domain.settlement.dto;

import java.math.BigDecimal;

public record MemberBalanceResponse(Long userId, BigDecimal totalPaid, BigDecimal totalOwed, BigDecimal netBalance) {
}
