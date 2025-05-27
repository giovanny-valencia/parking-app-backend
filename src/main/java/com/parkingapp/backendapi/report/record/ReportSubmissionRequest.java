package com.parkingapp.backendapi.report.record;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record ReportSubmissionRequest(
        // JWT -> get userid after authorization validation. Implement later

        // vehicle: state, plate
        @Valid
        @NotNull
        VehicleData vehicle



        // address: street, zip, notes, lat/long, jurisdiction: state, city

        // images: LP(1), Supporting images (1+)

        // description

        // created on


) {
}
