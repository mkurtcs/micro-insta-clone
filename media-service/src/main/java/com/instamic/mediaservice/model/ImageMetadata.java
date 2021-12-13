package com.instamic.mediaservice.model;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document
@RequiredArgsConstructor
public class ImageMetadata {

    @Id
    private String id;

    @CreatedBy
    private String username;

    @CreatedDate
    private Instant createdDate;

    @NonNull
    private String filename;

    @NonNull
    private String uri;

    @NonNull
    private String fileType;

    public static ImageMetadata createMetadataStoreImage(String filename, String uri, String fileType) {
        return new ImageMetadata(filename, uri, fileType);
    }
}
