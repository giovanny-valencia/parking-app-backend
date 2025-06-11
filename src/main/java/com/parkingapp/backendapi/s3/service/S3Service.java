package com.parkingapp.backendapi.s3.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedFileUpload;
import software.amazon.awssdk.transfer.s3.model.FileUpload;
import software.amazon.awssdk.transfer.s3.model.UploadFileRequest;
import software.amazon.awssdk.transfer.s3.progress.LoggingTransferListener;

@Service
public class S3Service {
  // These clients are now injected by Spring, thanks to S3Config
  private final S3AsyncClient s3AsyncClient;

  private final S3TransferManager transferManager;

  @Value("${aws.s3.bucketName}")
  private String bucketName;

  @Value("${aws.s3.region}") // This property is needed for constructing the public URL
  private String awsRegion;

  public S3Service(S3AsyncClient s3AsyncClient, S3TransferManager transferManager) {
    this.s3AsyncClient = s3AsyncClient;
    this.transferManager = transferManager;
  }

  /**
   * Uploads a MultipartFile to S3 using a specified object name (imageName).
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
    String originalFilename = file.getOriginalFilename();
    String fileExtension = "";
    if (originalFilename != null && originalFilename.lastIndexOf(".") != -1) {
      fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
    }

    // Construct the S3 key following the convention: reports/{reportId}/{objectName}.{extension}
    String s3Key =
        String.format("reports/%d/%s%s", reportId, imageName, fileExtension).toLowerCase();

    // Use Files.createTempFile for more robust temporary file creation
    Path tempFilePath = Files.createTempFile("upload-", fileExtension);
    File tempFile = tempFilePath.toFile();

    try {
      // Write MultipartFile content to temp file
      file.transferTo(tempFile);

      // Define the S3 PUT request
      PutObjectRequest putObjectRequest =
          PutObjectRequest.builder()
              .bucket(bucketName)
              .key(s3Key)
              .contentType(file.getContentType())
              .build();

      // Build the upload request for TransferManager
      UploadFileRequest uploadFileRequest =
          UploadFileRequest.builder()
              .putObjectRequest(putObjectRequest)
              .source(tempFilePath)
              .addTransferListener(LoggingTransferListener.create())
              .build();

      // Perform the upload and wait for completion
      FileUpload fileUpload = transferManager.uploadFile(uploadFileRequest);
      CompletableFuture<CompletedFileUpload> future = fileUpload.completionFuture();

      // .join() blocks until the upload is complete or an exception occurs
      CompletedFileUpload completedFileUpload = future.join();

      System.out.println("Successfully uploaded file to S3: " + s3Key);
      return s3Key; // absolute path to the object, used to make public urls
    } catch (Exception e) {
      throw new RuntimeException("Failed to upload file to S3: " + s3Key, e);
    } finally {
      // Ensure the temporary file is deleted
      try {
        Files.deleteIfExists(tempFilePath);
      } catch (IOException e) {
        // Log if temp file deletion fails, but don't rethrow
        System.err.println(
            "Failed to delete temporary file: " + tempFilePath + " - " + e.getMessage());
      }
    }
  }
}
