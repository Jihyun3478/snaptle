package com.snaptle.domain.personaldebt;

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
@Table(name = "personal_debts")
public class PersonalDebt extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trip_id", nullable = false)
    private Long tripId;

    @Column(name = "creditor_id", nullable = false)
    private Long creditorId;

    @Column(name = "debtor_id", nullable = false)
    private Long debtorId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false)
    private String reason;

    @Builder
    private PersonalDebt(Long tripId, Long creditorId, Long debtorId, BigDecimal amount, String reason) {
        this.tripId = tripId;
        this.creditorId = creditorId;
        this.debtorId = debtorId;
        this.amount = amount;
        this.reason = reason;
    }

    public static PersonalDebt create(Long tripId, Long creditorId, Long debtorId, BigDecimal amount,
                                       String reason) {
        return PersonalDebt.builder()
                .tripId(tripId)
                .creditorId(creditorId)
                .debtorId(debtorId)
                .amount(amount)
                .reason(reason)
                .build();
    }
}
