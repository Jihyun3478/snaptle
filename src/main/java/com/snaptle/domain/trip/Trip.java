package com.snaptle.domain.trip;

import com.snaptle.global.common.Currency;
import com.snaptle.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "trips",
        uniqueConstraints = @UniqueConstraint(name = "uk_trips_invite_code", columnNames = "invite_code")
)
public class Trip extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "base_currency", nullable = false)
    private Currency baseCurrency;

    @Column(name = "invite_code", nullable = false, length = 8)
    private String inviteCode;

    @Column(name = "ended", nullable = false)
    private boolean ended;

    @Builder
    private Trip(String name, LocalDate startDate, LocalDate endDate, Currency baseCurrency, String inviteCode) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.baseCurrency = baseCurrency;
        this.inviteCode = inviteCode;
        this.ended = false;
    }

    public static Trip create(String name, LocalDate startDate, LocalDate endDate, Currency baseCurrency,
                               String inviteCode) {
        return Trip.builder()
                .name(name)
                .startDate(startDate)
                .endDate(endDate)
                .baseCurrency(baseCurrency)
                .inviteCode(inviteCode)
                .build();
    }

    public void end() {
        this.ended = true;
    }
}
