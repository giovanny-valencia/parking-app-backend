package com.parkingapp.backendapi.report.record;

import com.parkingapp.backendapi.report.entity.Coordinates;
import jakarta.persistence.Embedded;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressData(

        @Size(max = 256, message = "Street address cannot exceed 256 characters")
        String streetAddress, // not always guaranteed

        @NotBlank(message = "Zip code is required")
        @Size(min = 5, max = 10, message = "Zip code must be between 5 and 10 characters")
        String zipCode,

        @Size(max = 128, message = "Location notes cannot exceed 128 characters")
        String locationNotes, // optional

        @Embedded
        @Valid
        Coordinates location,

        @Valid
        JurisdictionData jurisdiction
) {
}
