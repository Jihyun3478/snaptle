package com.snaptle.domain.settlement;

import com.snaptle.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "settlement_transfers")
public class SettlementTransfer extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "settlement_id", nullable = false)
    private Long settlementId;

    @Column(name = "from_user_id", nullable = false)
    private Long fromUserId;

    @Column(name = "to_user_id", nullable = false)
    private Long toUserId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Builder
    private SettlementTransfer(Long settlementId, Long fromUserId, Long toUserId, BigDecimal amount) {
        this.settlementId = settlementId;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.amount = amount;
    }

    public static SettlementTransfer of(Long settlementId, Long fromUserId, Long toUserId, BigDecimal amount) {
        return SettlementTransfer.builder()
                .settlementId(settlementId)
                .fromUserId(fromUserId)
                .toUserId(toUserId)
                .amount(amount)
                .build();
    }
}
