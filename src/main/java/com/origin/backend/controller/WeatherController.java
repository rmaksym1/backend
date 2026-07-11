package com.origin.backend.controller;

import com.origin.backend.dto.weather.WeatherResponseDto;
import com.origin.backend.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/weather")
@Tag(name = "Weather endpoints", description = "Endpoints for weather management")
public class WeatherController {
    private final WeatherService weatherService;

    @GetMapping
    @Operation(summary = "Get weather & info by date")
    public ResponseEntity<WeatherResponseDto> getWeather(
            @RequestParam(name = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(weatherService.getWeatherForDate(date));
    }
}
