package com.snaptle.global.ocr;

import com.snaptle.global.common.Currency;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReceiptOcrResult(
        String merchantName,
        BigDecimal amount,
        Currency currency,
        LocalDateTime paidAt
) {
}
