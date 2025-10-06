package com.parkingapp.backendapi.infrastructure.s3.service;

import java.io.IOException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class S3Service {

  private final S3UploadService s3UploadService;
  private final S3PresigningService s3PresigningService;

  /**
   * Delegation method for S3 Uploads
   *
   * @param file The MultipartFile to upload.
   * @param reportId The ID of the report this image belongs to.
   * @param imageName The specific name for the image (e.g., "licensePlate", "violationImage_1").
   *     The file extension will be automatically appended.
   * @return The full S3 key (path) of the uploaded object.
   * @throws IOException If there's an issue handling the file.
   * @throws RuntimeException If the S3 upload fails.
   */
  public String uploadFile(MultipartFile file, Long reportId, String imageName) throws IOException {
    return s3UploadService.uploadFile(file, reportId, imageName);
  }

  /**
   * Delegation method for S3 presignedUrl
   *
   * @param s3Key The S3 key (path) of the object.
   * @param expirationMinutes The duration in minutes for which the URL will be valid.
   * @return The pre-signed URL as a String.
   */
  public String generatePresignedUrl(String s3Key, long expirationMinutes) {
    return s3PresigningService.generatePresignedUrl(s3Key, expirationMinutes);
  }

  /**
   * Delegation method for S3 presignedUrl, default duration of 5 minutes
   *
   * @param s3Key The S3 key (path) of the object.
   * @return The pre-signed URL as a String.
   */
  public String generatePresignedUrl(String s3Key) {
    return s3PresigningService.generatePresignedUrl(s3Key);
  }
}
