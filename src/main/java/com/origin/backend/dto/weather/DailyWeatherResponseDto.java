package com.origin.backend.dto.weather;

import java.time.LocalDate;

public record DailyWeatherResponseDto(
        LocalDate date,
        String status,
        String description
) {}
