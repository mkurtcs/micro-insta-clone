package com.instamic.mediaservice.controller;

import com.instamic.mediaservice.model.ImageMetadata;
import com.instamic.mediaservice.model.response.ImageUploadResponse;
import com.instamic.mediaservice.service.ImageMetaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/images")
public class ImageUploadController {

    private final ImageMetaService imageMetaService;

    public ImageUploadController(ImageMetaService imageMetaService) {
        this.imageMetaService = imageMetaService;
    }


    @PostMapping("/upload")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ImageUploadResponse> uploadImage(@RequestParam("image") MultipartFile image,
                                                           Principal principal) {

        ImageMetadata imageMetadata = imageMetaService.upload(image, principal.getName());

        return new ResponseEntity<>(
                new ImageUploadResponse(imageMetadata.getFilename(),
                                        imageMetadata.getUri(),
                                        imageMetadata.getFileType()), HttpStatus.OK);
    }

}
