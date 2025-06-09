package com.parkingapp.backendapi.report.service;

import com.parkingapp.backendapi.common.service.ImageValidationService;
import com.parkingapp.backendapi.report.entity.Report;
import com.parkingapp.backendapi.report.mapper.ReportRequestMapper;
import com.parkingapp.backendapi.report.record.ReportSubmissionRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Orchestration service for handling report submissions.
 *
 * <p>This service acts as an entry point for report-related operations. It accepts Data Transfer
 * Objects (DTOs) from the controller, performs necessary validations, converts DTOs into domain
 * entities, and then delegates to appropriate subdomain services for further processing and
 * persistence.
 */
@Service
@AllArgsConstructor
public class ReportService {

  private final ReportProcessingService reportProcessingService;
  private final ImageValidationService imageValidationService;

  private final ReportRequestMapper reportRequestMapper;
  private final Validator validator;

  private void validateDto(ReportSubmissionRequest reportRequest) {
    Set<ConstraintViolation<ReportSubmissionRequest>> violations =
        validator.validate(reportRequest);
    if (!violations.isEmpty()) {
      String errorMessage =
          violations.stream()
              .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
              .reduce("", (a, b) -> a + (a.isEmpty() ? "" : ", ") + b);
      throw new IllegalArgumentException(
          "Validation failed: " + errorMessage); // handle this better
    }
  }

  /**
   * Initial steps of processing a new report submitted by a user.
   *
   * <p>This method performs initial validation steps (DTO and image validation) before converting
   * the incoming DTO into a domain entity. It then delegates the entity and associated files to the
   * {@link ReportProcessingService} for further processing and persistence.
   *
   * @param reportRequest DTO containing the string-based elements of the report. See {@link
   *     ReportSubmissionRequest} for details.
   * @param licensePlateImageFile {@code MultipartFile} representing the license plate image.
   * @param violationImageFiles List of {@code MultipartFile}s for the violation images.
   * @throws IOException If an I/O error occurs during image processing or interaction with external
   *     services (e.g., S3).
   */
  // TODO: feedback for user report submission.
  public void submitNewReport(
      ReportSubmissionRequest reportRequest,
      MultipartFile licensePlateImageFile,
      List<MultipartFile> violationImageFiles)
      throws IOException {
    //  after successful mapping from controller, validate the dto field restrictions
    validateDto(reportRequest);

    // validate the images before converting to entity
    imageValidationService.validateImage(licensePlateImageFile);
    violationImageFiles.forEach(imageValidationService::validateImage);

    Report report = reportRequestMapper.toEntity(reportRequest);

    reportProcessingService.processReport(report, licensePlateImageFile, violationImageFiles);
  }
}
