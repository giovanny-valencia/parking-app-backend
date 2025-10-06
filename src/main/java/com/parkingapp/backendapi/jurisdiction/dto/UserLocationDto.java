package com.parkingapp.backendapi.jurisdiction.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record UserLocationDto(
        @NotNull
        @DecimalMin(value = "-180.0", message = "Longitude must be greater than or equal to -180.")
        @DecimalMax(value = "180.0", message = "Longitude must be less than or equal to 180.")
        BigDecimal longitude,

        @NotNull
        @DecimalMin(value = "-90.0", message = "Latitude must be greater than or equal to -90.")
        @DecimalMax(value = "90.0", message = "Latitude must be less than or equal to 90.")
        BigDecimal latitude
) {}
