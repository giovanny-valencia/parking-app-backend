package com.parkingapp.backendapi.s3.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

  @Mock
  private S3UploadService s3UploadService;

  @Mock
  private S3PresigningService s3PresigningService;

  @InjectMocks
  private S3Service s3Service;

  @Test
  void s3Service_isCreated_successfully() {
    assertNotNull(s3Service);
  }
}
