package com.origin.backend.dto.participant;

public record ParticipantResponse(
        Long id,
        String name,
        Long packId,
        Integer instructorHours
) {}
