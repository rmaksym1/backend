package com.origin.backend.controller;

import com.origin.backend.dto.WeatherResponseDto;
import com.origin.backend.service.WeatherService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/weather")
public class WeatherController {
    private final WeatherService weatherService;

    @GetMapping
    public ResponseEntity<WeatherResponseDto> getWeather(
            @RequestParam(name = "date", required = false) LocalDate date
    ) {
        return ResponseEntity.ok(weatherService.getWeatherForDate(date));
    }
}
