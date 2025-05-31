package com.parkingapp.backendapi.report.service;

import com.parkingapp.backendapi.report.entity.Jurisdiction;
import com.parkingapp.backendapi.report.entity.Report;
import com.parkingapp.backendapi.report.entity.ReportAddress;
import com.parkingapp.backendapi.report.entity.ReportImage;
import com.parkingapp.backendapi.report.entity.Vehicle;
import com.parkingapp.backendapi.report.mapper.ReportSubmissionRequestMapper;
import com.parkingapp.backendapi.report.record.ReportSubmissionRequest;
import com.parkingapp.backendapi.report.repository.JurisdictionRepository;
import com.parkingapp.backendapi.report.repository.ReportRepository;
import com.parkingapp.backendapi.report.repository.VehicleRepository;
import com.parkingapp.backendapi.user.entity.User;
import com.parkingapp.backendapi.user.repository.UserRepository;
//import com.parkingapp.backendapi.user.service.TestUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportSubmissionRequestMapper reportSubmissionRequestMapper;
    private final UserRepository userRepository;
    private final JurisdictionRepository jurisdictionRepository;
    private final VehicleRepository vehicleRepository;

    @Transactional
    /*
        report fields:
         address -- Jurisdiction MUST be handled via DB.
         vehicle -- check DB for existing, if exists then set. Else handled
         reportingUser -- grab from DB
         images[] -- set to report

         createdOn -- now()
         notes -- null on create
         assignedOfficer -- null on create
         UpdatedOn -- null
     */
    public void processReportSubmissionRequest(ReportSubmissionRequest request){
        Report report = reportSubmissionRequestMapper.toEntity(request);
        System.out.println("dto -> entity:");

        System.out.println("id: " + report.getId());
        System.out.println("vehicle: " + report.getVehicle());
        System.out.println("address: " + report.getAddress());
        System.out.println("assigned officer: " + report.getAssignedOfficer());
        System.out.println("reportingUser: " + report.getReportingUser());
        System.out.println("images: ");
        for (ReportImage i : report.getImages()){
            System.out.println("i: " + i);
        }
        System.out.println("desc: " + report.getDescription());
        System.out.println("notes: " + report.getNotes());
        System.out.println("createdOn: " + report.getCreatedOn());
        System.out.println("updatedOn: " + report.getUpdatedOn());

        //address -- jurisdiction MUST be grabbed from DB
        Jurisdiction jurisdiction = jurisdictionRepository.findByStateAndCity(
                report.getAddress().getJurisdiction().getState(),
                report.getAddress().getJurisdiction().getCity()
        );
        if (jurisdiction == null){ // not supported, exit early
            System.out.println("Jurisdiction not supported");
            return;
        }

        // update the report address to be associated with jurisdiction
        ReportAddress newAddress = report.getAddress();
        newAddress.setJurisdiction(jurisdiction);
        //report.setAddress(newAddress);

        // vehicle -- grab from db to associate, or new vehicle.
        System.out.println("Getting vehicle:");
        Vehicle reportVehicle = report.getVehicle();
        Vehicle vehicle = vehicleRepository.findByPlateStateAndPlateNumber(
                reportVehicle.getPlateState(),
                reportVehicle.getPlateNumber()
        );
        System.out.println("repo search: " + vehicle);
        if (vehicle != null){ // existing vehicle
            report.setVehicle(vehicle);
            System.out.println("assigned existing vehicle to report");
        }

        // All images are unique -- need to assign them
        if(report.getImages() != null){
            for (ReportImage image : report.getImages()){
                image.setReport(report);
            }
        }

        // test user -- but will need to fetch user from db
        // JWT -> authorized -> get userId
        User testUser = userRepository.findByEmail("testuser@example.com");
        System.out.println("user data: " + testUser);
        report.setReportingUser(testUser);

        reportRepository.save(report);

        System.out.println("report added to DB");
    }
}
