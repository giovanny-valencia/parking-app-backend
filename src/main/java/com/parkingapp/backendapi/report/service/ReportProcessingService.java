package com.parkingapp.backendapi.report.service;

import com.parkingapp.backendapi.common.exception.DuplicateReportException;
import com.parkingapp.backendapi.jurisdiction.entity.Jurisdiction;
import com.parkingapp.backendapi.jurisdiction.repository.JurisdictionRepository;
import com.parkingapp.backendapi.jurisdiction.service.JurisdictionCacheService;
import com.parkingapp.backendapi.report.entity.Report;
import com.parkingapp.backendapi.report.entity.ReportImage;
import com.parkingapp.backendapi.report.entity.Status;
import com.parkingapp.backendapi.report.entity.Vehicle;
import com.parkingapp.backendapi.report.repository.ReportRepository;
import com.parkingapp.backendapi.report.repository.VehicleRepository;
import com.parkingapp.backendapi.s3.service.S3Service;
import com.parkingapp.backendapi.user.entity.User;
import com.parkingapp.backendapi.user.repository.UserRepository;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
@Slf4j
public class ReportProcessingService {

  private final UserRepository userRepository;
  private final ReportRepository reportRepository;
  private final VehicleRepository vehicleRepository;
  private final JurisdictionRepository jurisdictionRepository;

  private final JurisdictionCacheService
      jurisdictionCacheService; // TODO: move this to jurisdiction service

  private final S3Service s3Service;

  // Simple v1 uniqueness test: reported vehicle currently has an 'open' report in the DB.
  // TODO:  in the future it will include same vehicle in a given radius
  private boolean isDuplicateReport(Vehicle vehicle) {
    List<Status> activeStatuses = Arrays.asList(Status.OPEN, Status.ASSIGNED);
    return reportRepository.existsByVehiclePlateStateAndVehiclePlateNumberAndStatusIn(
        vehicle.getPlateState(), vehicle.getPlateNumber(), activeStatuses);
  }

  /**
   * The actual processing service of a user's report
   *
   * <p>handles data validation, database retrieval for assignment, saves photos to s3
   *
   * @param report the report domain entity
   * @param licensePlateImageFile the MultipartFile of the license plate
   * @param violationImageFiles the List of MultipartFile of violation images
   * @throws IOException s3UploadService can throw IOException if the image fails integrity tests
   */
  @Transactional
  public void processReport(
      Report report, MultipartFile licensePlateImageFile, List<MultipartFile> violationImageFiles)
      throws IOException {

    //  ensure that report is not a duplicate before preceding
    if (isDuplicateReport(report.getVehicle())) {
      log.warn(
          "Attempted to process duplicate report for vehicle: {} - {}",
          report.getVehicle().getPlateState(),
          report.getVehicle().getPlateNumber());
      throw new DuplicateReportException("Duplicate report found for this vehicle and status.");
    }

    // validates and assigns, throws if not valid
    report.getAddress().setJurisdiction(getJurisdictionIfValid(report));

    /*  Coordinates must be within specified Jurisdiction
        Point-in-Polygon (Geofencing) *AFTER DEMO*
        1. Obtain Geographic Boundary Data
        2. Store the Data
        3. Perform Point-in-Polygon Checks
    */

    //  vehicle data validation: if it exists in the DB, retrieve it and set it.
    //  Otherwise, new vehicle will be added to DB later
    setVehicleIfExists(report);

    //  Assign user to report before saving, user will always be in DB as it was authenticated then
    // JWT authorized
    //  Worth validation checking if user was not found? Seems redundant
    //  * Note: JWT might be sufficient to associate the user preventing server hit? *
    // TODO: replace this with jwt
    User user =
        userRepository
            .findByEmail("testuser@example.com")
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "Authenticated user not found in database (data inconsistency): "));
    report.setReportingUser(user);

    // after all data is valid, save the report. Reference it to get the ID & set (associate) images
    Report savedReport = reportRepository.save(report);

    // upload the images to s3 using the reportId identifier, then update the report with images
    String licensePlateS3Key =
        s3Service.uploadFile(licensePlateImageFile, savedReport.getId(), "licensePlateImage");
    ReportImage licensePlateImage = new ReportImage();
    licensePlateImage.setUrl(licensePlateS3Key);
    licensePlateImage.setReport(report);
    savedReport.getImages().add(licensePlateImage);

    for (int i = 0; i < violationImageFiles.size(); i++) {
      MultipartFile currentViolationImageFile = violationImageFiles.get(i);

      try {
        String violationImageS3Key =
            s3Service.uploadFile(
                currentViolationImageFile, savedReport.getId(), "violationImage_" + (i + 1));
        ReportImage violationImage = new ReportImage();
        violationImage.setUrl(violationImageS3Key);
        violationImage.setReport(savedReport);
        savedReport.getImages().add(violationImage);
      } catch (IOException e) {
        throw new RuntimeException("Failed to upload violation image " + (i + 1) + " to S3.", e);
      }
    }
  }

  private void setVehicleIfExists(Report report) {
    Vehicle existingVehicle =
        vehicleRepository.findByPlateStateAndPlateNumber(
            report.getVehicle().getPlateState(), report.getVehicle().getPlateNumber());
    if (existingVehicle
        != null) { // vehicle found in DB so associate the current report's vehicle to the DB
      // vehicle
      report.setVehicle(existingVehicle);
    }
  }

  /*  Jurisdiction validation
   *   The jurisdictions are cached on server start up for optimization.
   *   If jurisdiction isn't found in the cache, then it's not in the DB.
   *   Client (should) check for this, so the report didn't come from the app.
   */
  private Jurisdiction getJurisdictionIfValid(Report report) {
    String reportState = report.getAddress().getJurisdiction().getState().toString();
    String reportCity = report.getAddress().getJurisdiction().getCity();

    //  validates the provided jurisdiction against a cache to avoid hitting server
    Jurisdiction detachedJurisdiction =
        jurisdictionCacheService
            .findJurisdictionByStateAndCity(reportState, reportCity)
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Jurisdiction " + reportState + ", " + reportCity + " is not supported."));

    //  if valid then retrieves the jurisdiction from the DB in order to be a managed version
    return jurisdictionRepository
        .findById(detachedJurisdiction.getId())
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Error: Jurisdiction with ID "
                        + detachedJurisdiction.getId()
                        + " found in cache but not in current database. Data inconsistency"));
  }
}
