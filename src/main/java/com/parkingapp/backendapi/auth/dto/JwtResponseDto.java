package com.parkingapp.backendapi.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record JwtResponseDto(
    @NotBlank String token,
    @NotBlank String type,
    @NotBlank String email,
    @NotBlank String accountType,
    @NotNull Long expiresIn) {}
