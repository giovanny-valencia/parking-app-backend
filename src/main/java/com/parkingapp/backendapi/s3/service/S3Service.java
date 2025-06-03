package com.parkingapp.backendapi.s3.service;


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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class S3Service {
    @Value("${aws.s3.bucketName}")
    private String bucketName; // This property is still needed for bucket name in S3 operations

    @Value("${aws.s3.region}") // This property is needed for constructing the public URL
    private String awsRegion;

    // These clients are now injected by Spring, thanks to S3Config
    private final S3AsyncClient s3AsyncClient;
    private final S3TransferManager transferManager;

    public S3Service(S3AsyncClient s3AsyncClient, S3TransferManager transferManager) {
        this.s3AsyncClient = s3AsyncClient;
        this.transferManager = transferManager;
    }

    /**
     * Uploads a MultipartFile to S3 with a unique key.
     *
     * @param file The MultipartFile to upload.
     * @return The S3 key (path) of the uploaded object.
     * @throws IOException If there's an issue handling the file or during upload.
     */
    public String uploadFile(MultipartFile file) throws IOException{
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String s3Key = "uploads/" + UUID.randomUUID().toString() + fileExtension; // Unique key for S3

        // Create a temporary file to transfer MultipartFile content to
        File tempFile = File.createTempFile("upload-", fileExtension);

        try{
            file.transferTo(tempFile); // Write MultipartFile content to temp file

            // Define the S3 PUT request
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(file.getContentType())
                    .build();

            // Build the upload request for TransferManager
            UploadFileRequest uploadFileRequest = UploadFileRequest.builder()
                    .putObjectRequest(putObjectRequest)
                    .source(Path.of(tempFile.getAbsolutePath()))
                    .addTransferListener(LoggingTransferListener.create())
                    .build();

            // Perform the upload and wait for completion
            FileUpload fileUpload = transferManager.uploadFile(uploadFileRequest);
            CompletableFuture<CompletedFileUpload> future = fileUpload.completionFuture();
            future.join();

            System.out.println("Successfully uploaded file to S3: " + s3Key);
            return s3Key;
        }
        finally {
            tempFile.delete();
        }
    }
}
