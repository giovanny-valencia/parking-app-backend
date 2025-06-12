package com.parkingapp.backendapi.report.record;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.Instant;

public record ReportSummaryDto(
    @NotNull Long id,
    @Valid @NotNull CoordinatesDto location,
    @NotNull @PastOrPresent Instant createdOn) {}
