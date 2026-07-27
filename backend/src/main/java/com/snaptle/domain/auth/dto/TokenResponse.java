package com.snaptle.domain.auth.dto;

public record TokenResponse(String accessToken, String tokenType) {

    public static TokenResponse ofBearer(String accessToken) {
        return new TokenResponse(accessToken, "Bearer");
    }
}
