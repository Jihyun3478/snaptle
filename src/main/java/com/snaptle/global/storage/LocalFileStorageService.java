package com.snaptle.global.storage;

import com.snaptle.global.exception.ErrorCode;
import com.snaptle.global.exception.SnaptleException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LocalFileStorageService implements FileStorageService {

    private final Path uploadDir;
    private final String publicBaseUrl;

    public LocalFileStorageService(FileStorageProperties properties) {
        this.uploadDir = Path.of(properties.uploadDir());
        this.publicBaseUrl = properties.publicBaseUrl();
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new UncheckedIOException("파일 업로드 디렉터리를 생성할 수 없습니다.", e);
        }
    }

    @Override
    public StoredFile store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new SnaptleException(ErrorCode.INVALID_FILE);
        }

        try {
            byte[] content = file.getBytes();
            String extension = extractExtension(file.getOriginalFilename());
            String storedName = UUID.randomUUID() + extension;
            Path target = uploadDir.resolve(storedName);
            Files.write(target, content);

            String url = publicBaseUrl.endsWith("/")
                    ? publicBaseUrl + storedName
                    : publicBaseUrl + "/" + storedName;

            return new StoredFile(url, content, file.getContentType());
        } catch (IOException e) {
            throw new SnaptleException(ErrorCode.INVALID_FILE);
        }
    }

    private String extractExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return "";
        }
        return originalFilename.substring(originalFilename.lastIndexOf('.'));
    }
}
