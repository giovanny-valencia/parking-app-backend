package com.parkingapp.backendapi.report.record;

import com.parkingapp.backendapi.jurisdiction.record.JurisdictionData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AddressDto(
    @Size(max = 256, message = "Street address cannot exceed 256 characters")
    String streetAddress, // not always guaranteed

    @NotBlank(message = "Zip code is required")
    @Size(min = 5, max = 10, message = "Zip code must be between 5 and 10 characters")
    String zipCode,

    @Size(max = 128, message = "Location notes cannot exceed 128 characters")
    String locationNotes, // optional

    @Valid @NotNull(message = "Location coordinates are required")
    CoordinatesDto location,

    @Valid @NotNull(message = "Jurisdiction data is required")
    JurisdictionData jurisdiction) {}
