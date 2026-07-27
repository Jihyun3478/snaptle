package com.snaptle.global.exchangerate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ExchangeRatePairResponse(
        String result,
        @JsonProperty("conversion_rate") BigDecimal conversionRate
) {
}
