package com.parkingapp.backendapi.report.controller;

import com.parkingapp.backendapi.report.record.JurisdictionData;
import com.parkingapp.backendapi.report.record.ReportSubmissionRequest;
import com.parkingapp.backendapi.report.service.JurisdictionService;
import com.parkingapp.backendapi.report.service.ReportService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping
    public ResponseEntity<Void> postUserReport(
          @Valid @RequestBody ReportSubmissionRequest reportSubmissionRequest
    ){
        reportService.processReportSubmissionRequest(reportSubmissionRequest);

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
