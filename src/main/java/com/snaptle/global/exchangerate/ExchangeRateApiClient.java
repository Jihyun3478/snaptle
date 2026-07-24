package com.snaptle.global.exchangerate;

import com.snaptle.global.common.Currency;
import java.math.BigDecimal;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ExchangeRateApiClient implements ExchangeRateClient {

    private static final Logger log = LoggerFactory.getLogger(ExchangeRateApiClient.class);
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6";

    private final ExchangeRateProperties properties;
    private final RestClient restClient;

    public ExchangeRateApiClient(ExchangeRateProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.create(BASE_URL);
    }

    @Override
    public Optional<BigDecimal> getRate(Currency from, Currency to) {
        if (from == to) {
            return Optional.of(BigDecimal.ONE);
        }
        if (!properties.isConfigured()) {
            return Optional.empty();
        }

        try {
            ExchangeRatePairResponse response = restClient.get()
                    .uri("/{key}/pair/{from}/{to}", properties.exchangeRateApiKey(), from, to)
                    .retrieve()
                    .body(ExchangeRatePairResponse.class);

            if (response == null || !"success".equals(response.result()) || response.conversionRate() == null) {
                log.warn("환율 조회 실패: {} -> {}, response={}", from, to, response);
                return Optional.empty();
            }
            return Optional.of(response.conversionRate());
        } catch (Exception e) {
            log.warn("환율 API 호출 실패: {} -> {}", from, to, e);
            return Optional.empty();
        }
    }
}
