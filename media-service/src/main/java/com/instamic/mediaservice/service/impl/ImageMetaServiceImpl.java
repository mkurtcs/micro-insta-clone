package com.instamic.mediaservice.service.impl;

import com.instamic.mediaservice.model.ImageMetadata;
import com.instamic.mediaservice.repository.ImageMetadataRepository;
import com.instamic.mediaservice.service.ImageMetaService;
import com.instamic.mediaservice.service.ImageStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Service
@Slf4j
public class ImageMetaServiceImpl implements ImageMetaService {

    private final ImageStorageService imageStorageService;
    private final ImageMetadataRepository imageMetadataRepository;

    public ImageMetaServiceImpl(ImageStorageService imageStorageService, ImageMetadataRepository repository) {
        this.imageStorageService = imageStorageService;
        this.imageMetadataRepository = repository;
    }


    @Override
    public ImageMetadata upload(MultipartFile file, String username) {
        log.info("ImageMetaServiceImpl.upload {}, {}", file, username);

        ImageMetadata storageMetadata = imageStorageService.store(file, username);
        return imageMetadataRepository.save(storageMetadata);
    }

}
