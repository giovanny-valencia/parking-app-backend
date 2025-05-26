package com.parkingapp.backendapi.report.controller;

import com.parkingapp.backendapi.report.dto.JurisdictionDto;
import com.parkingapp.backendapi.report.service.JurisdictionService;
import com.parkingapp.backendapi.report.service.ReportService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<List<JurisdictionDto>> getJurisdiction(){
        System.out.println("Jurisdiction api reached");

        return ResponseEntity.ok(jurisdictionService.getSupportedJurisdictions());
    }
}
