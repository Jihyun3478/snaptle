package com.snaptle.global.ocr;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
record GeminiRequest(List<Content> contents, GenerationConfig generationConfig) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    record Content(List<Part> parts) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    record Part(String text, InlineData inlineData) {
        static Part ofText(String text) {
            return new Part(text, null);
        }

        static Part ofImage(String mimeType, String base64Data) {
            return new Part(null, new InlineData(mimeType, base64Data));
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    record InlineData(String mimeType, String data) {
    }

    record GenerationConfig(String responseMimeType) {
    }
}
