package com.parkingapp.backendapi.jurisdiction.controller;

import com.parkingapp.backendapi.jurisdiction.record.JurisdictionData;
import com.parkingapp.backendapi.jurisdiction.service.JurisdictionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/jurisdictions")
public class JurisdictionController {

    private final JurisdictionService jurisdictionService;

    /**
     * Retrieves a list of all jurisdictions currently supported by the application.
     *
     * @return A {@code ResponseEntity} containing a list of {@code JurisdictionData} objects and an
     *     HTTP status of 200 OK.
     */
    @GetMapping()
    public ResponseEntity<List<JurisdictionData>> getJurisdiction() {
        return ResponseEntity.ok(jurisdictionService.getSupportedJurisdictions());
    }
}
