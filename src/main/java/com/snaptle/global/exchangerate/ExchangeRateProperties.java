package com.snaptle.global.exchangerate;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "snaptle.external-api")
public record ExchangeRateProperties(String exchangeRateApiKey) {

    public boolean isConfigured() {
        return exchangeRateApiKey != null && !exchangeRateApiKey.isBlank();
    }
}
