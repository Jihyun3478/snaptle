package com.snaptle.domain.trip.dto;

import jakarta.validation.constraints.NotBlank;

public record JoinTripRequest(@NotBlank String inviteCode) {
}
