package com.parkingapp.backendapi.s3.service;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Service
public class S3PresigningService {
  private final S3Presigner s3Presigner;
  private final String bucketName;

  // Resorted to manual constructor as lombok failed
  public S3PresigningService(
      S3Presigner s3Presigner, @Value("${aws.s3.bucketName}") String bucketName) {
    this.s3Presigner = s3Presigner;
    this.bucketName = bucketName;
  }

  /**
   * Generates a pre-signed URL for a given S3 object key. The URL will be valid for a specified
   * duration.
   *
   * @param s3Key The S3 key (path) of the object.
   * @param expirationMinutes The duration in minutes for which the URL will be valid.
   * @return The pre-signed URL as a String.
   */
  public String generatePresignedUrl(String s3Key, long expirationMinutes) {
    GetObjectRequest getObjectRequest =
        GetObjectRequest.builder().bucket(bucketName).key(s3Key).build();

    PresignedGetObjectRequest presignedRequest =
        s3Presigner.presignGetObject(
            r ->
                r.getObjectRequest(getObjectRequest)
                    .signatureDuration(Duration.ofMinutes(expirationMinutes)));

    return presignedRequest.url().toString();
  }

  /** Generates a pre-signed URL with a default expiration of 5 minutes. */
  public String generatePresignedUrl(String s3Key) {
    return generatePresignedUrl(s3Key, 5);
  }
}
