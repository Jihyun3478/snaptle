package com.snaptle.domain.trip.dto;

import com.snaptle.domain.trip.Trip;
import com.snaptle.global.common.Currency;
import java.time.LocalDate;
import java.util.List;

public record TripResponse(
        Long id,
        String name,
        LocalDate startDate,
        LocalDate endDate,
        Currency baseCurrency,
        String inviteCode,
        boolean ended,
        List<TripMemberResponse> members
) {

    public static TripResponse of(Trip trip, List<TripMemberResponse> members) {
        return new TripResponse(
                trip.getId(),
                trip.getName(),
                trip.getStartDate(),
                trip.getEndDate(),
                trip.getBaseCurrency(),
                trip.getInviteCode(),
                trip.isEnded(),
                members
        );
    }
}
