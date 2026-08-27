package com.origin.backend.dto.booking;

import com.origin.backend.dto.participant.ParticipantRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record CreateBookingRequest(
        @NotBlank(message = "Full name cannot be blank!")
        String fullName,
        @NotNull(message = "Rental date cannot be null!")
        LocalDate rentalDate,
        @Schema(description = "Surfing pack issuance time", example = "08:00", type = "string")
        @NotNull(message = "Issuance time cannot be null!")
        LocalTime issuanceTime,
        @Email(message = "Email should be valid!")
        @NotBlank(message = "Email cannot be blank!")
        String email,
        @Schema(
                description = "Customer phone number",
                example = "+1-182-1846-1927"
        )
        @Pattern(regexp = "^\\+?(?:\\d[ \\-\\(\\)]*){7,15}\\d$",
                message = "Phone number must be valid!")
        @NotBlank(message = "Phone number cannot be blank!")
        String phoneNumber,
        @NotNull(message = "Participant list cannot be null!")
        @Size(min = 1, max = 10, message = "Number of participants must be between 1 and 10!")
        List<@Valid ParticipantRequest> participants
) {}
