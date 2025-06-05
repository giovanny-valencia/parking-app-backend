package com.parkingapp.backendapi.report.service;

import com.parkingapp.backendapi.common.enums.State;
import com.parkingapp.backendapi.common.service.ImageValidationService;
import com.parkingapp.backendapi.jurisdiction.entity.Jurisdiction;
import com.parkingapp.backendapi.jurisdiction.repository.JurisdictionRepository;
import com.parkingapp.backendapi.jurisdiction.service.JurisdictionCacheService;
import com.parkingapp.backendapi.report.entity.Report;
import com.parkingapp.backendapi.report.entity.ReportImage;
import com.parkingapp.backendapi.report.entity.Status;
import com.parkingapp.backendapi.report.entity.Vehicle;
import com.parkingapp.backendapi.report.mapper.ReportSubmissionRequestMapper;
import com.parkingapp.backendapi.report.record.ReportSubmissionRequest;
import com.parkingapp.backendapi.report.repository.ReportRepository;
import com.parkingapp.backendapi.report.repository.VehicleRepository;
import com.parkingapp.backendapi.s3.service.S3Service;
import com.parkingapp.backendapi.user.entity.User;
import com.parkingapp.backendapi.user.repository.UserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class ReportService {

    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final VehicleRepository vehicleRepository;
    private final JurisdictionRepository jurisdictionRepository;

    private final ReportSubmissionRequestMapper reportSubmissionRequestMapper;

    private final ImageValidationService imageValidationService;
    private final JurisdictionCacheService jurisdictionCacheService;
    private final S3Service s3Service;

    private final ObjectMapper objectMapper;
    private final Validator validator; // Inject the Validator

    // Simple v1 uniqueness test: reported vehicle currently has an 'open' report in the DB.
    // in the future it will be same vehicle in a given radius
    private boolean isDuplicateReport(Vehicle vehicle){
        List<Status> activeStatuses = Arrays.asList(Status.OPEN, Status.ASSIGNED);
        return reportRepository.existsByVehiclePlateStateAndVehiclePlateNumberAndStatusIn(
                vehicle.getPlateState(),
                vehicle.getPlateNumber(),
                activeStatuses
        );
    }

    @Transactional
    public void processReportSubmissionRequest(
            String  reportJson,
            MultipartFile licensePlateImageFile,
            List<MultipartFile> violationImageFiles
    ) throws IOException {

        //  convert reportJson into dto
        ReportSubmissionRequest reportRequest = convertReportJsonToDto(reportJson, objectMapper);

        //  after successful mapping, validate the dto field restrictions
        validateDto(reportRequest);

        /*  data validation
        *   starting with images before converting dto into entity model
        *   seems slightly more optimized as there's no point to convert to entity if the images fail validation
        */
        imageValidationService.validateImage(licensePlateImageFile);
        violationImageFiles.forEach(imageValidationService::validateImage);

        Report report = reportSubmissionRequestMapper.toEntity(reportRequest);

        //  ensure that report is not a duplicate before preceding
        if (isDuplicateReport(report.getVehicle())){
            System.out.println("Duplicate report in system");
            //throw new DuplicateReportException("Duplicate report found for this vehicle and status.");
            return;
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

        //  Assign user to report before saving, user will always be in DB as it was authenticated then JWT authorized
        //  Worth validation checking if user was not found? Seems redundant
        //  * Note: JWT might be sufficient to associate the user preventing server hit? *
        User user = userRepository.findByEmail("testuser@example.com");
        report.setReportingUser(user);

        // after all data is valid, save the report. Reference it to get the ID & set (associate) images
        Report savedReport =  reportRepository.save(report);

        // upload the images to s3 using the reportId identifier, then update the report with images
        String licensePlateS3Key = s3Service.uploadFile(
                licensePlateImageFile,
                savedReport.getId(),
                "licensePlateImage"
        );
        ReportImage licensePlateImage = new ReportImage();
        licensePlateImage.setUrl(licensePlateS3Key);
        licensePlateImage.setReport(report);
        savedReport.getImages().add(licensePlateImage);


        for (int i = 0; i < violationImageFiles.size(); i++) {
            MultipartFile currentViolationImageFile = violationImageFiles.get(i);

            try{
                String violationImageS3Key = s3Service.uploadFile(
                        currentViolationImageFile,
                        savedReport.getId(),
                        "violationImage_" + (i + 1)
                        );
                ReportImage violationImage = new ReportImage();
                violationImage.setUrl(violationImageS3Key);
                violationImage.setReport(savedReport);
                savedReport.getImages().add(violationImage);
            }catch (IOException e){
                throw new RuntimeException("Failed to upload violation image " + (i + 1) + " to S3.", e);
            }
        }

        System.out.println("done");
    }

    private void setVehicleIfExists(Report report) {
        Vehicle existingVehicle = vehicleRepository.findByPlateStateAndPlateNumber(
                report.getVehicle().getPlateState(),
                report.getVehicle().getPlateNumber()
        );
        if (existingVehicle != null){ // vehicle found in DB so associate the current report's vehicle to the DB vehicle
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
        String reportCity =  report.getAddress().getJurisdiction().getCity();

        //  validates the provided jurisdiction against a cache to avoid hitting server
        Jurisdiction detachedJurisdiction = jurisdictionCacheService
                .findJurisdictionByStateAndCity( reportState, reportCity)
                .orElseThrow(()-> new IllegalArgumentException(
                "Jurisdiction " + reportState + ", " + reportCity + " is not supported."
        ));

        //  if valid then retrieves the jurisdiction from the DB in order to be a managed version
        return jurisdictionRepository.findById(detachedJurisdiction.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Error: Jurisdiction with ID " + detachedJurisdiction.getId() +
                        " found in cache but not in current database. Data inconsistency"
        ));
    }

    private void validateDto(ReportSubmissionRequest reportRequest) {
        Set<ConstraintViolation<ReportSubmissionRequest>> violations = validator.validate(reportRequest);
        if (!violations.isEmpty()){
            String errorMessage = violations.stream()
                    .map(violation -> violation.getPropertyPath() + ": " +
                            violation.getMessage())
                    .reduce("", (a, b) -> a + (a.isEmpty() ? "" : ", ") + b);
            throw new IllegalArgumentException("Validation failed: " + errorMessage); // handle this better
        }
    }

    private static ReportSubmissionRequest convertReportJsonToDto(String reportJson, ObjectMapper objectMapper)
            throws JsonProcessingException {
        ReportSubmissionRequest reportRequest;
        reportRequest = objectMapper.readValue(reportJson, ReportSubmissionRequest.class);
        return reportRequest;
    }
}
