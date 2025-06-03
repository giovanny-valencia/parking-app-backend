package com.parkingapp.backendapi.report.service;

import com.parkingapp.backendapi.report.entity.Jurisdiction;
import com.parkingapp.backendapi.report.entity.Report;
import com.parkingapp.backendapi.report.entity.ReportAddress;
import com.parkingapp.backendapi.report.entity.ReportImage;
import com.parkingapp.backendapi.report.entity.Status;
import com.parkingapp.backendapi.report.entity.Vehicle;
import com.parkingapp.backendapi.report.mapper.ReportSubmissionRequestMapper;
import com.parkingapp.backendapi.report.record.ReportSubmissionRequest;
import com.parkingapp.backendapi.report.repository.JurisdictionRepository;
import com.parkingapp.backendapi.report.repository.ReportRepository;
import com.parkingapp.backendapi.report.repository.VehicleRepository;
import com.parkingapp.backendapi.s3.service.S3Service;
import com.parkingapp.backendapi.user.entity.User;
import com.parkingapp.backendapi.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportSubmissionRequestMapper reportSubmissionRequestMapper;
    private final UserRepository userRepository;
    private final JurisdictionRepository jurisdictionRepository;
    private final VehicleRepository vehicleRepository;
    private final S3Service s3Service;

    // Simple v1 uniqueness test: reported vehicle currently has an 'open' report in the DB.
    // in the future it will be same vehicle in a given radius
    private boolean isDuplicateReport(Vehicle vehicle){
        List<Status> activeStatuses = Arrays.asList(Status.OPEN, Status.ASSIGNED);
        return reportRepository.existsByVehicleAndStatusIn(vehicle, activeStatuses);
    }

    @Transactional
    public void processReportSubmissionRequest(
            //ReportSubmissionRequest request,
            MultipartFile licensePlateImageFile
            //List<MultipartFile> violationImageFiles
                                               ){
        System.out.println("sending image to s3");

        try{
            s3Service.uploadFile(licensePlateImageFile);
        }
        catch (IOException e){
            System.out.println("error from s3: " + e.getMessage());
        }



//        Report report = reportSubmissionRequestMapper.toEntity(request);
////        System.out.println("dto -> entity:");
////        System.out.println("id: " + report.getId());
////        System.out.println("vehicle: " + report.getVehicle());
////        System.out.println("address: " + report.getAddress());
////        System.out.println("assigned officer: " + report.getAssignedOfficer());
////        System.out.println("reportingUser: " + report.getReportingUser());
////        System.out.println("images: ");
////        for (ReportImage i : report.getImages()){
////            System.out.println("i: " + i);
////        }
////        System.out.println("desc: " + report.getDescription());
////        System.out.println("notes: " + report.getNotes());
////        System.out.println("createdOn: " + report.getCreatedOn());
////        System.out.println("updatedOn: " + report.getUpdatedOn());
//
//        //address -- jurisdiction must be grabbed from DB
//        Jurisdiction jurisdiction = jurisdictionRepository.findByStateAndCity(
//                report.getAddress().getJurisdiction().getState(),
//                report.getAddress().getJurisdiction().getCity()
//        );
//        if (jurisdiction == null){ // not supported, exit early
//            System.out.println("Jurisdiction not supported");
//            return;
//        }
//        ReportAddress newAddress = report.getAddress();
//        newAddress.setJurisdiction(jurisdiction); // update the report address to be associated with jurisdiction
//
//        // vehicle -- grab from db to associate, or new vehicle.
//        Vehicle reportVehicle = report.getVehicle();
//        Vehicle vehicle = vehicleRepository.findByPlateStateAndPlateNumber(
//                reportVehicle.getPlateState(),
//                reportVehicle.getPlateNumber()
//        );
//        if (vehicle != null){ // existing vehicle
//            report.setVehicle(vehicle);
//        }
//
//        if (isDuplicateReport(vehicle)){
//            System.out.println("Duplicate report in system");
//            return;
//        }
//
//        // All images are unique -- need to associate them to report
//        if(report.getImages() != null){
//            for (ReportImage image : report.getImages()){
//                image.setReport(report);
//            }
//        }
//
//        // test user -- but will need to fetch user from db
//        // JWT -> authorized -> get userId
//        User testUser = userRepository.findByEmail("testuser@example.com");
//        report.setReportingUser(testUser);
//
//        reportRepository.save(report);
    }
}
