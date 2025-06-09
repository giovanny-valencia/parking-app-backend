package com.parkingapp.backendapi.report.record;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VehicleData(
    @NotBlank(message = "License plate state is required")
        @Size(min = 2, max = 2, message = "License plate state must be a 2-character abbreviation")
        String state,
    @NotBlank(message = "License plate number is required")
        @Size(max = 10, message = "License plate number must be between 1 and 10 characters")
        String plateNumber) {}
