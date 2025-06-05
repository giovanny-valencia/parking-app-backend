package com.parkingapp.backendapi.report.record;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReportSubmissionRequest(
        // JWT -> get userid after authorization validation. Implement later

        // vehicle: state, plate
        @Valid
        @NotNull
        VehicleData vehicle,

        // address: street, zip, notes, lat/long, jurisdiction: state, city
        @Valid
        @NotNull
        AddressData address,

        @NotBlank
        //@NotNull
        @Size(max = 256)
        String description
) {
}
