package com.origin.backend.dto.participant;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ParticipantRequest(
        String name,
        @NotNull(message = "Pack id cannot be null!")
        Long packId,
        @Min(value = 0, message = "Instructor hours cannot be negative!")
        @Max(value = 12, message = "Instructor hours cannot be more than 12!")
        Integer instructorHours
) {}
