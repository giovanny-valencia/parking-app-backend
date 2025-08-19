package com.parkingapp.backendapi.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for a JWT authentication response.
 *
 * <p>This object contains the JWT token and its type. The client can then decode the token to
 * access the user's claims.
 */
public record JwtResponseDto(@NotBlank String token, @NotBlank String type) {}
