package com.snaptle.domain.trip.dto;

import com.snaptle.domain.user.User;

public record TripMemberResponse(Long userId, String nickname, String profileImageUrl) {

    public static TripMemberResponse from(User user) {
        return new TripMemberResponse(user.getId(), user.getNickname(), user.getProfileImageUrl());
    }
}
