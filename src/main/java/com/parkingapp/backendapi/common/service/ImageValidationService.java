package com.parkingapp.backendapi.common.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

// todo: could add image size requirements after deciding on image constraints / resizing.
@Service
public class ImageValidationService {

    @Value("${spring.servlet.multipart.max-file-size}")
    private DataSize maxFileSize;

    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/webp"
    );
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "png", "webp"
    );

    public void validateImage(MultipartFile imageFile){
        // 1. Basic Checks: Null, Size and Name
        if (imageFile == null || imageFile.isEmpty()){
            throw new IllegalArgumentException("Image file cannot be empty.");
        }

        // Now using the injected 'maxFileSize'
        if (imageFile.getSize() > this.maxFileSize.toBytes()){
            throw new IllegalArgumentException("Image file size exceeds the maximum allowed");
        }

        String originalFilename = imageFile.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")){
            throw new IllegalArgumentException("Image file must have an extension.");
        }
        String fileExtension = originalFilename.substring(originalFilename
                .lastIndexOf(".") + 1)
                .toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
            throw new IllegalArgumentException("Invalid file extension. Only JPG, PNG, WebP are allowed.");
        }

        // 2. MIME Type Check (Client-provided, but good first filter)
        String contentType = imageFile.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Invalid image content type: " + contentType);
        }

        // 3. Attempt to read content
        try{
            BufferedImage image = ImageIO.read(imageFile.getInputStream());
            if (image == null){
                throw new IllegalArgumentException("File is not a valid or recognized image format.");
            }
        }catch (IOException e){
            throw new IllegalArgumentException("Could not read image file due to I/O error or corruption. " + e.getMessage(), e);
        }
    }
}
