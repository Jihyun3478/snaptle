package com.snaptle.global.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "snaptle.jwt")
public record JwtProperties(String secret, long accessTokenExpirationMs) {
}
