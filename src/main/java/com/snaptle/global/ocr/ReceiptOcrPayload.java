package com.snaptle.global.ocr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
record ReceiptOcrPayload(String merchantName, String amount, String currency, String paidAt) {
}
