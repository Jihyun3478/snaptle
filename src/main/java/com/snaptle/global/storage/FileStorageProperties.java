package com.snaptle.global.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "snaptle.file")
public record FileStorageProperties(String uploadDir, String publicBaseUrl) {
}
