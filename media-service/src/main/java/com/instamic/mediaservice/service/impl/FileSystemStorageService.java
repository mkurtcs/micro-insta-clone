package com.instamic.mediaservice.service.impl;

import com.instamic.mediaservice.exception.InvalidFileException;
import com.instamic.mediaservice.exception.InvalidFileNameException;
import com.instamic.mediaservice.exception.StorageException;
import com.instamic.mediaservice.model.ImageMetadata;
import com.instamic.mediaservice.service.ImageStorageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class FileSystemStorageService implements ImageStorageService {

    @Value("${file.upload-dir}")
    private String uploadDirectory;

    @Value("${file.path.prefix}")
    private String filePathPrefix;

    @Autowired
    private Environment environment;


    @Override
    public ImageMetadata store(MultipartFile file, String username) {

        log.info("FileSystemStorageService.store started. filename: {}, username: {}",
                file.getOriginalFilename(), username);

        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        if (file.isEmpty()) {
            log.warn("Failed to store empty file {}", filename);
            throw new InvalidFileException("The file is empty!");
        }

        if (filename.contains("..")) {
            // Security check
            log.warn("Can not store file with relative path {}", filename);
            throw new InvalidFileNameException(
                    "Cannot store file with relative path outside current directory " + filename);
        }

        try {

            String extension = FilenameUtils.getExtension(filename);
            String newFilename = UUID.randomUUID() + "." + extension;

            InputStream inputStream = file.getInputStream();

            // Every photo is stored in a user specific folder.
            Path userDir = Paths.get(uploadDirectory + username);

            if (Files.notExists(userDir))
                Files.createDirectory(userDir);

            Files.copy(inputStream, userDir.resolve(newFilename), StandardCopyOption.REPLACE_EXISTING);

            String port = environment.getProperty("local.server.port");
            String host = InetAddress.getLocalHost().getHostName();

            String imageUrl = String.format("http://%s:%s%s/%s/%s",
                    host, port, filePathPrefix, username, newFilename);

            log.info("successfully stored file {} location {}", filename, imageUrl);

            return ImageMetadata.createMetadataStoreImage(filename, imageUrl, file.getContentType());

        } catch (IOException e) {
            log.error("failed to store file {} error: {}", filename, e);
            throw new StorageException("Failed to store file: " + filename, e);
        }
    }
}
