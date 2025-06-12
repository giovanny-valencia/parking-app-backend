package com.parkingapp.backendapi.report.record;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportOfficerViewDto {
  @Valid @NotNull AddressDto addressDto;
  @Valid @NotNull VehicleDto vehicleDto;
  @Valid @NotNull List<ReportImageDto> reportImageDto;
  @NotBlank String description;
  @NotNull Instant createdOn;
}
