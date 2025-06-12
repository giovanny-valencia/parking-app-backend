package com.parkingapp.backendapi.s3.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

@Configuration
public class S3Config {

  @Value("${aws.s3.region}")
  private String awsRegion;

  // IMPORTANT: For local development. In production, prefer IAM roles on EC2/ECS/EKS.
  @Value("${aws.accessKeyId}")
  private String accessKeyId;

  @Value("${aws.secretAccessKey}")
  private String secretAccessKey;

  @Bean
  public S3AsyncClient s3AsyncClient() {
    StaticCredentialsProvider credentialsProvider =
        StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey));

    return S3AsyncClient.builder()
        .region(Region.of(awsRegion))
        .credentialsProvider(credentialsProvider)
        // Use Netty for the underlying HTTP client for async operations
        .httpClientBuilder(
            NettyNioAsyncHttpClient.builder()
                .writeTimeout(Duration.ofSeconds(30)) // Example timeout
                .readTimeout(Duration.ofSeconds(30)))
        .build();
  }

  @Bean
  public S3TransferManager s3TransferManager(S3AsyncClient s3AsyncClient) {
    return S3TransferManager.builder().s3Client(s3AsyncClient).build();
  }

  @Bean
  public S3Presigner s3Presigner() {
    StaticCredentialsProvider credentialsProvider =
            StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey));

    return S3Presigner.builder()
            .region(Region.of(awsRegion))
            .credentialsProvider(credentialsProvider)
            .build();
  }
}
