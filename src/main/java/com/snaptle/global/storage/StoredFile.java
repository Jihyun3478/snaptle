package com.snaptle.global.storage;

public record StoredFile(String url, byte[] content, String contentType) {
}
