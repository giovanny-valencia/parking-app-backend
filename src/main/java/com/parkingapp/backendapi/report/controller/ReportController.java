package com.parkingapp.backendapi.report.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.parkingapp.backendapi.jurisdiction.record.JurisdictionData;
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

import java.io.IOException;
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> postUserReport(
            @RequestPart(value = "report") String reportJson,
            @RequestPart(value = "licensePlateImage", required = true) MultipartFile licensePlateImage,
            @RequestPart(value = "violationImages", required = true) List<MultipartFile> violationImages
    ) throws IOException {
        reportService.processReportSubmissionRequest(reportJson, licensePlateImage, violationImages);

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
