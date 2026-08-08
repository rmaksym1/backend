package com.origin.backend.dto.weather;

import java.time.LocalDate;

public record DailyWeatherResponse(
        LocalDate date,
        String status,
        String description
) {}
