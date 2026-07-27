package com.snaptle.global.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    StoredFile store(MultipartFile file);
}
