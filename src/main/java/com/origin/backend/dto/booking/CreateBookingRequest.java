package com.origin.backend.dto.booking;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateBookingRequest(
        @NotBlank(message = "Full name cannot be blank!")
        String fullName,
        @Email(message = "Email should be valid!")
        @NotBlank(message = "Email cannot be blank!")
        String email,
        @NotBlank(message = "Phone number cannot be blank!")
        String phoneNumber,
        @Min(value = 0, message = "Instructor hours cannot be negative!")
        Integer instructorHours,
        @NotNull(message = "Pack id cannot be null!")
        Long packId
) {}
