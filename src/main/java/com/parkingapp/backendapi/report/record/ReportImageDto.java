package com.parkingapp.backendapi.report.record;

import jakarta.validation.constraints.NotBlank;

public record ReportImageDto(@NotBlank String url) {}
