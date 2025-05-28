package com.parkingapp.backendapi.report.record;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record JurisdictionData(

        @NotBlank
        @Size(min = 2, max = 2, message = "State must be a 2-letter code")
        String state,

        @NotBlank
        @Size(min = 2, max = 50, message = "City name length must be between 2 and 50 characters")
        String city) {}
