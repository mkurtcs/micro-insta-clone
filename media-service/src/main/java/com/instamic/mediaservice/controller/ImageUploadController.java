package com.instamic.mediaservice.controller;

import com.instamic.mediaservice.model.ImageMetadata;
import com.instamic.mediaservice.model.response.ImageUploadResponse;
import com.instamic.mediaservice.service.ImageMetaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
public class ImageUploadController {

    private final ImageMetaService imageMetaService;

    public ImageUploadController(ImageMetaService imageMetaService) {
        this.imageMetaService = imageMetaService;
    }


    @PostMapping
    public ResponseEntity<ImageUploadResponse> uploadImage(@RequestParam("image") MultipartFile image,
                                                           @AuthenticationPrincipal Principal principal) {

        ImageMetadata imageMetadata = imageMetaService.upload(image, principal.getName());

        return new ResponseEntity<>(
                new ImageUploadResponse(imageMetadata.getFilename(),
                                        imageMetadata.getUri(),
                                        imageMetadata.getFileType()), HttpStatus.OK);
    }

}
