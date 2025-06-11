package com.parkingapp.backendapi.report.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parkingapp.backendapi.jurisdiction.record.JurisdictionData;
import com.parkingapp.backendapi.jurisdiction.service.JurisdictionService;
import com.parkingapp.backendapi.report.record.ReportSubmissionRequest;
import com.parkingapp.backendapi.report.record.ReportSummary;
import com.parkingapp.backendapi.report.service.ReportService;
import java.io.IOException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for handling report-related operations. Provides endpoints for: - Submitting new
 * user reports - Retrieve active report summaries (ids and location coordinates) - Retrieve
 * specific report details given the id
 */
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

  private final JurisdictionService jurisdictionService;
  private final ReportService reportService;

  private final ObjectMapper objectMapper;

  private static ReportSubmissionRequest convertReportJsonToDto(
      String reportJson, ObjectMapper objectMapper) throws JsonProcessingException {
    ReportSubmissionRequest reportRequest;
    reportRequest = objectMapper.readValue(reportJson, ReportSubmissionRequest.class);
    return reportRequest;
  }

  /**
   * Retrieves a list of all jurisdictions currently supported by the application.
   *
   * @return A {@code ResponseEntity} containing a list of {@code JurisdictionData} objects and an
   *     HTTP status of 200 OK.
   */
  @GetMapping("jurisdiction")
  public ResponseEntity<List<JurisdictionData>> getJurisdiction() {
    return ResponseEntity.ok(jurisdictionService.getSupportedJurisdictions());
  }

  /**
   * Retrieves a summary of active reports.
   *
   * <p>TODO: fix the return and rewrite this doc
   *
   * <p>TODO: Implement logic to filter reports by jurisdiction or geographical radius.
   * AuthenticationPrincipal can be used to get user details for personalized access.
   *
   * @return A {@code ResponseEntity} containing a list of {@code Report} summaries and an HTTP
   *     status of 200 OK. Currently, returns an empty response.
   */
  @GetMapping("active-summaries") // better naming convention?
  public ResponseEntity<List<ReportSummary>> getActiveReportSummaries(
      //   @AuthenticationPrincipal UserDetails principal
      ) {
    List<ReportSummary> activeReportSummaries = reportService.getActiveReportSummaries();

    return ResponseEntity.ok(activeReportSummaries);
  }

  /**
   * Submits a new user report, including report details and associated images.
   *
   * <p>Workaround for React Native Expo multipart/form-data limitations: The 'report' payload is
   * received as a JSON string rather than directly binding to a DTO. This approach was adopted
   * after encountering issues where the React Native Expo client failed to properly map the
   * 'report' part to a DTO, even though standard tools like Postman succeeded. The JSON string is
   * manually parsed into a ReportSubmissionRequest DTO here, with DTO and image validation handled
   * by ReportService. This solution is pragmatic for now but should be re-evaluated if the client's
   * form data submission mechanism is refactored.
   *
   * @param reportJson The report details as a JSON string.
   * @param licensePlateImage The image file of the license plate.
   * @param violationImages A list of image files showing the violation.
   * @return A {@code ResponseEntity} with an HTTP status of 200 OK upon successful submission.
   * @throws IOException If there's an issue with file processing during submission.
   */
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Void> postUserReport(
      @RequestPart(value = "report") String reportJson,
      @RequestPart(value = "licensePlateImage") MultipartFile licensePlateImage,
      @RequestPart(value = "violationImages") List<MultipartFile> violationImages)
      throws IOException {

    ReportSubmissionRequest reportRequest = convertReportJsonToDto(reportJson, objectMapper);
    reportService.submitNewReport(reportRequest, licensePlateImage, violationImages);

    /*
     * TODO: feedback for user report submission.
     * Future improvements could include:
     * 1. Indicating if the report was a unique submission or a duplicate.
     * 2. Providing a clear confirmation of successful persistence to the database.
     * This feedback will become critical when integrating with features like a payments service,
     * where users need explicit confirmation of a valid and processed entry.
     */
    return ResponseEntity.ok().build();
  }
}
