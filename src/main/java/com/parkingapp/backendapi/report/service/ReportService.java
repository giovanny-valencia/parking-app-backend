package com.parkingapp.backendapi.report.service;

import com.parkingapp.backendapi.common.service.ImageValidationService;
import com.parkingapp.backendapi.report.entity.Report;
import com.parkingapp.backendapi.report.mapper.ReportRequestMapper;
import com.parkingapp.backendapi.report.mapper.ReportSummaryMapper;
import com.parkingapp.backendapi.report.mapper.ReportViewMapper;
import com.parkingapp.backendapi.report.dto.ReportOfficerViewDto;
import com.parkingapp.backendapi.report.dto.ReportRequestDto;
import com.parkingapp.backendapi.report.dto.ReportSummaryDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Orchestration service for handling report submissions.
 *
 * <p>This service acts as an entry point for report-related operations. It accepts Data Transfer
 * Objects (DTOs) from the controller, performs necessary validations, converts DTOs into domain
 * entities, and then delegates to appropriate subdomain services for further processing and
 * persistence, converts subdomain return values back into DTOs to send to the controller.
 */
@Service
@AllArgsConstructor
public class ReportService {

  private static final Logger log = LoggerFactory.getLogger(ReportService.class);
  private final ReportProcessingService reportProcessingService;
  private final ActiveParkingReportService activeParkingReportService;
  private final ImageValidationService imageValidationService;

  private final ReportRequestMapper reportRequestMapper;
  private final ReportSummaryMapper reportSummaryMapper;
  private final ReportViewMapper reportViewMapper;

  private final Validator validator;

  /**
   * Initial steps of processing a new report submitted by a user.
   *
   * <p>This method performs initial validation steps (DTO and image validation) before converting
   * the incoming DTO into a domain entity. It then delegates the entity and associated files to the
   * {@link ReportProcessingService} for further processing and persistence.
   *
   * @param reportRequest DTO containing the string-based elements of the report. See {@link
   *     ReportRequestDto} for details.
   * @param licensePlateImageFile {@code MultipartFile} representing the license plate image.
   * @param violationImageFiles List of {@code MultipartFile}s for the violation images.
   * @throws IOException If an I/O error occurs during image processing or interaction with external
   *     services (e.g., S3).
   */
  // TODO: feedback for user report submission.
  public void submitNewReport(
      ReportRequestDto reportRequest,
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

  /**
   * Orchestrates the subdomain service call to retrieve a quick summary of the active reports in a
   * given state,city jurisdiction
   *
   * @return List of ReportSummaryDto for the active reports given a jurisdiction
   */
  public List<ReportSummaryDto> getActiveReportSummaries() {
    // TODO: recheck and update these comments after demo for prod

    // officer will send JWT as bearer token -- this will produce their associated jurisdiction
    // (grabbed here)
    // TODO: current officer lat/long location and possibly a desired radius -- will only provide
    // active reports in this range

    //  for now use testing jurisdiction -- simulate JWT data
    // note: in actual this might be a JurisdictionDto point? -- worry about this later
    String J_TEST_STATE = "NJ"; // TODO: FIX THIS
    String J_TEST_CITY = "Union City"; // TODO: FIX THIS

    List<ReportSummaryDto> activeReportSummaries =
        activeParkingReportService.retrieveReportsByJurisdiction(J_TEST_STATE, J_TEST_CITY).stream()
            .map(reportSummaryMapper::toDto)
            .toList();

    System.out.println("logging active summaries");
    activeReportSummaries.forEach(System.out::println);

    return activeReportSummaries;
  }

  /**
   * TODO: potential validation?
   *
   * @param id selected report id
   * @return full report details
   */
  public ReportOfficerViewDto getSelectedReportDetails(Long id) {

    return activeParkingReportService.retrieveReportDetails(id);
  }

  /**
   * Helper function for submitNewReport that validates the mapped dto of a Report
   *
   * @param reportRequest dto representation of a Report
   */
  private void validateDto(ReportRequestDto reportRequest) {
    Set<ConstraintViolation<ReportRequestDto>> violations = validator.validate(reportRequest);
    if (!violations.isEmpty()) {
      String errorMessage =
          violations.stream()
              .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
              .reduce("", (a, b) -> a + (a.isEmpty() ? "" : ", ") + b);
      throw new IllegalArgumentException(
          "Validation failed: " + errorMessage); // handle this better
    }
  }
}
