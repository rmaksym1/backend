package com.origin.backend.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record WeatherResponseDto(
        LocalDate date,
        String title,
        double waterTemperature,
        String bestTime,
        String status,
        String description,
        List<Double> chartData
) {}
