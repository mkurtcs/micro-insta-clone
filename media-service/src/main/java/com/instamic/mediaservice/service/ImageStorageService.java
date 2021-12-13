package com.instamic.mediaservice.service;

import com.instamic.mediaservice.model.ImageMetadata;
import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {

    ImageMetadata store(MultipartFile file, String username);
}
