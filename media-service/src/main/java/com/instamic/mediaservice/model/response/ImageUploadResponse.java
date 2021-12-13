package com.instamic.mediaservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImageUploadResponse {

    private String fileName;
    private String uri;
    private String fileType;

}
