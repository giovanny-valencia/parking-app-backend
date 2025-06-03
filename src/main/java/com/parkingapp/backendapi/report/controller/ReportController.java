package com.parkingapp.backendapi.report.controller;

import com.parkingapp.backendapi.report.record.JurisdictionData;
import com.parkingapp.backendapi.report.service.JurisdictionService;
import com.parkingapp.backendapi.report.service.ReportService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;
    private final JurisdictionService jurisdictionService;

    @GetMapping("jurisdiction")
    public ResponseEntity<List<JurisdictionData>> getJurisdiction(){
        return ResponseEntity.ok(jurisdictionService.getSupportedJurisdictions());
    }

//    // **************   TESTING
//    public static class SimpleMessage {
//        private String message;
//        public String getMessage() { return message; }
//        public void setMessage(String message) { this.message = message; }
//        @Override public String toString() { return "SimpleMessage{message='" + message + "'}"; }
//    }
//    // ***************************



    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    //@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE) // this was used for the report json data
    public ResponseEntity<Void> postUserReport(
            // The image file
            @RequestPart(value = "licensePlateImage", required = true) MultipartFile licensePlateImage
    ){

        System.out.println(licensePlateImage.getName());
        System.out.println(licensePlateImage.getResource());
        System.out.println(licensePlateImage.getSize());
        System.out.println(licensePlateImage.isEmpty());
        System.out.println(licensePlateImage.getOriginalFilename());
        System.out.println(licensePlateImage.getContentType());

        // Process the licensePlateImage if it exists
        if (licensePlateImage != null && !licensePlateImage.isEmpty()) {
            System.out.println("Received license plate image: " + licensePlateImage.getOriginalFilename() +
                    " (Size: " + licensePlateImage.getSize() + " bytes)");
            // Here you would save the file, e.g., to disk or cloud storage
            // Example: try { licensePlateImage.transferTo(new File("path/to/save/" + licensePlateImage.getOriginalFilename())); } catch (IOException e) { e.printStackTrace(); }
        } else {
            System.out.println("No license plate image received.");
        }

        reportService.processReportSubmissionRequest(licensePlateImage);




        /*
            No current feedback on:
            1. if the report was unique
            2. if the report was successfully posted

            In the future, when payments service is implemented, the user
            needs to know if the report they submitted was a valid entry and/or if was even
            successfully posted to the DB.
         */
        return ResponseEntity.ok().build();
    }

}
