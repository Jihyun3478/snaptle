package com.snaptle.domain.expense.dto;

import com.snaptle.global.common.Currency;
import com.snaptle.global.ocr.ReceiptOcrResult;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OcrRecognizeResponse(
        String receiptImageUrl,
        boolean recognized,
        String merchantName,
        BigDecimal amount,
        Currency currency,
        LocalDateTime paidAt
) {

    public static OcrRecognizeResponse recognized(String receiptImageUrl, ReceiptOcrResult result) {
        return new OcrRecognizeResponse(
                receiptImageUrl, true, result.merchantName(), result.amount(), result.currency(), result.paidAt()
        );
    }

    public static OcrRecognizeResponse notRecognized(String receiptImageUrl) {
        return new OcrRecognizeResponse(receiptImageUrl, false, null, null, null, null);
    }
}
