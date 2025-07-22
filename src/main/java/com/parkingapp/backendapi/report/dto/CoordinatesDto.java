package com.parkingapp.backendapi.report.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal; // Recommended for precise lat/long

public record CoordinatesDto(
    @NotNull(message = "Latitude is required")
        @Min(value = -90, message = "Latitude must be between -90 and 90 degrees.")
        @Max(value = 90, message = "Latitude must be between -90 and 90 degrees.")
        BigDecimal latitude, // Using BigDecimal is often preferred for coordinates for precision
    @NotNull(message = "Longitude is required")
        @Min(value = -180, message = "Longitude must be between -180 and 180 degrees.")
        @Max(value = 180, message = "Longitude must be between -180 and 180 degrees.")
        BigDecimal longitude // Using BigDecimal is often preferred for coordinates for precision
    ) {}
