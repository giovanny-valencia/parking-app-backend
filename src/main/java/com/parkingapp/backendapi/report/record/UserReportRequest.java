package com.parkingapp.backendapi.report.record;

public record UserReportRequest(
        String[] images, // license plate image followed by supporting report images
        String plateNumber,
        String description
) {}
