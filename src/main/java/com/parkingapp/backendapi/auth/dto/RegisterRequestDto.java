package com.parkingapp.backendapi.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

public record RegisterRequestDto(
    @NotBlank @Email String email,
    @NotBlank String password,
    @NotBlank String confirmPassword,

    // for simplicity, names are standard letters. No Spaces, apostrophes, or hyphens for now
    @NotBlank @Pattern(regexp = "^[a-zA-Z]+$") String firstName,
    @NotBlank @Pattern(regexp = "^[a-zA-Z]+$") String lastName,
    @NotNull @Past LocalDate dateOfBirth,
    @NotNull Boolean agreedToTerms) {}
