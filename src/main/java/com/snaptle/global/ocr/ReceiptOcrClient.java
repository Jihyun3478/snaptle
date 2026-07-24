package com.snaptle.global.ocr;

import java.util.Optional;

public interface ReceiptOcrClient {

    Optional<ReceiptOcrResult> recognize(byte[] imageBytes, String contentType);
}
