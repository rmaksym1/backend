package com.origin.backend.dto.participant;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ParticipantRequest(
        String name,
        @NotNull(message = "Pack id cannot be null!")
        Long packId,
        @NotNull(message = "Instructor hours cannot be null!")
        @PositiveOrZero(message = "Instructor hours cannot be negative!")
        @Max(value = 12, message = "Instructor hours cannot be more than 12!")
        Integer instructorHours
) {}
