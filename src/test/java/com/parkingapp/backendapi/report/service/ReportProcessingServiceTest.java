package com.parkingapp.backendapi.report.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.parkingapp.backendapi.common.service.ImageValidationService;
import com.parkingapp.backendapi.jurisdiction.repository.JurisdictionRepository;
import com.parkingapp.backendapi.jurisdiction.service.JurisdictionCacheService;
import com.parkingapp.backendapi.report.repository.ReportRepository;
import com.parkingapp.backendapi.report.repository.VehicleRepository;
import com.parkingapp.backendapi.s3.service.S3Service;
import com.parkingapp.backendapi.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReportProcessingServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private JurisdictionRepository jurisdictionRepository;

    @Mock
    private JurisdictionCacheService jurisdictionCacheService;

    @Mock
    private S3Service s3Service;

    @Mock
    private ImageValidationService imageValidationService;

    @InjectMocks
    private ReportProcessingService reportProcessingService;

    @Test
    void reportProcessingService_isCreated_successfully() {
        assertNotNull(reportProcessingService);
    }
}
