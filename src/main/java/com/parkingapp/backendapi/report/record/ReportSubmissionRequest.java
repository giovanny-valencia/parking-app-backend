package com.parkingapp.backendapi.report.record;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

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

        // images: LP(1), Supporting images (1+)
        @NotBlank(message = "License plate image URL/identifier is required")
        @NotNull
        String licensePlateImage,
        @NotNull(message = "Violation images list cannot be null")
        @NotEmpty(message = "At least one violation image is required")
        List<String> violationImages,

        // description
        @NotBlank
        @NotNull
        @Size(min = 1, max = 256)
        String description
) {
}
