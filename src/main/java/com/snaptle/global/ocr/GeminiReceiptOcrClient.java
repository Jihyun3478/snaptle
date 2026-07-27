package com.snaptle.global.ocr;

import com.snaptle.global.common.Currency;
import com.snaptle.global.config.RestClients;
import tools.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GeminiReceiptOcrClient implements ReceiptOcrClient {

    private static final Logger log = LoggerFactory.getLogger(GeminiReceiptOcrClient.class);
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models";
    private static final String PROMPT = """
            이 이미지는 여행 중 결제한 영수증 사진이다. 아래 필드를 인식해서 JSON으로만 응답하라.
            - merchantName: 가맹점명 (문자열, 인식 불가 시 null)
            - amount: 결제 총액 (숫자만, 통화 기호/천단위 콤마 제외한 문자열, 인식 불가 시 null)
            - currency: 통화 코드. 반드시 KRW, USD, JPY, CNY, EUR 중 하나 (인식 불가 시 null)
            - paidAt: 결제 일시. ISO-8601 형식(yyyy-MM-ddTHH:mm:ss), 시간 인식 불가 시 00:00:00, 날짜 인식 불가 시 null

            응답 예시: {"merchantName":"스타벅스","amount":"15000","currency":"KRW","paidAt":"2026-08-01T14:30:00"}
            """;

    private final GeminiOcrProperties properties;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public GeminiReceiptOcrClient(GeminiOcrProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.restClient = RestClients.createWithTimeout(BASE_URL);
    }

    @Override
    public Optional<ReceiptOcrResult> recognize(byte[] imageBytes, String contentType) {
        if (!properties.isConfigured()) {
            return Optional.empty();
        }

        try {
            GeminiRequest request = new GeminiRequest(
                    List.of(new GeminiRequest.Content(List.of(
                            GeminiRequest.Part.ofText(PROMPT),
                            GeminiRequest.Part.ofImage(contentType, Base64.getEncoder().encodeToString(imageBytes))
                    ))),
                    new GeminiRequest.GenerationConfig("application/json")
            );

            GeminiResponse response = restClient.post()
                    .uri("/{model}:generateContent?key={key}", properties.modelOrDefault(), properties.geminiApiKey())
                    .body(request)
                    .retrieve()
                    .body(GeminiResponse.class);

            String text = extractText(response);
            if (text == null) {
                return Optional.empty();
            }

            return parse(text);
        } catch (Exception e) {
            log.warn("영수증 OCR 인식 실패", e);
            return Optional.empty();
        }
    }

    private String extractText(GeminiResponse response) {
        if (response == null || response.candidates() == null || response.candidates().isEmpty()) {
            return null;
        }
        GeminiResponse.Content content = response.candidates().get(0).content();
        if (content == null || content.parts() == null || content.parts().isEmpty()) {
            return null;
        }
        return content.parts().get(0).text();
    }

    private Optional<ReceiptOcrResult> parse(String text) {
        try {
            ReceiptOcrPayload payload = objectMapper.readValue(text, ReceiptOcrPayload.class);

            BigDecimal amount = parseAmount(payload.amount());
            Currency currency = parseCurrency(payload.currency());
            LocalDateTime paidAt = parsePaidAt(payload.paidAt());

            return Optional.of(new ReceiptOcrResult(payload.merchantName(), amount, currency, paidAt));
        } catch (Exception e) {
            log.warn("영수증 OCR 응답 파싱 실패: {}", text, e);
            return Optional.empty();
        }
    }

    private BigDecimal parseAmount(String amount) {
        if (amount == null || amount.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(amount.replaceAll("[^0-9.]", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Currency parseCurrency(String currency) {
        if (currency == null || currency.isBlank()) {
            return null;
        }
        try {
            return Currency.valueOf(currency.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private LocalDateTime parsePaidAt(String paidAt) {
        if (paidAt == null || paidAt.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(paidAt);
        } catch (Exception e) {
            return null;
        }
    }
}
