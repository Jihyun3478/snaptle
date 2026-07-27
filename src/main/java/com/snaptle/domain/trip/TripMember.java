package com.snaptle.domain.trip;

import com.snaptle.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "trip_members",
        uniqueConstraints = @UniqueConstraint(name = "uk_trip_members_trip_user", columnNames = {"trip_id", "user_id"})
)
public class TripMember extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trip_id", nullable = false)
    private Long tripId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Builder
    private TripMember(Long tripId, Long userId) {
        this.tripId = tripId;
        this.userId = userId;
    }

    public static TripMember of(Long tripId, Long userId) {
        return TripMember.builder()
                .tripId(tripId)
                .userId(userId)
                .build();
    }
}
