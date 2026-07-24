package com.snaptle.global.ocr;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "snaptle.external-api")
public record GeminiOcrProperties(String geminiApiKey, String geminiModel) {

    public boolean isConfigured() {
        return geminiApiKey != null && !geminiApiKey.isBlank();
    }

    public String modelOrDefault() {
        return (geminiModel == null || geminiModel.isBlank()) ? "gemini-2.0-flash" : geminiModel;
    }
}
