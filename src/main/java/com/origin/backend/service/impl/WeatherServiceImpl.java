package com.origin.backend.service.impl;

import com.origin.backend.dto.WeatherResponseDto;
import com.origin.backend.service.WeatherService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;

@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {

    private final WebClient webClient;

    @Override
    public WeatherResponseDto getWeatherForDate(LocalDate localDate) {
        JsonNode response = fetchWeather(localDate);
        JsonNode hourly = response.path("hourly");

        // fetching wave heights from API response array into single List
        List<Double> waveHeights = new ArrayList<>();
        hourly.path("wave_height")
                .forEach(node
                        -> waveHeights.add(node.asDouble())
                );

        // fetching water temperatures from API response array into single List
        List<Double> waterTemps = new ArrayList<>();
        hourly.path("sea_surface_temperature")
                .forEach(node
                        -> waterTemps.add(node.asDouble())
                );

        // calculating max wave height
        Double maxWaveHeight = waveHeights.stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(0.0);

        // calculating average water temperature
        double avgWaterTemp = waterTemps.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(18.0);
        int finalWaterTemp = (int) Math.round(avgWaterTemp);

        // logic for status & description
        String status;
        String description;

        if (maxWaveHeight > 1.5) {
            status = "Huge Swell";
            description = "Pro surfers only";
        } else if (maxWaveHeight > 0.7) {
            status = "Good Waves";
            description = "Perfect for everyone";
        } else {
            status = "Calm water";
            description = "Good for beginners";
        }

        // logic for best hour calculation
        int bestHour = 12;
        double maxDayWave = 0.0;
        for (int i = 8; i <= 18; i++) {
            if (i < waveHeights.size() && waveHeights.get(i) > maxDayWave) {
                maxDayWave = waveHeights.get(i);
                bestHour = i;
            }
        }

        String bestTime = String.format("%02d:00 - %02d:00", bestHour, (bestHour + 3) % 24);

        // returning weather response
        return WeatherResponseDto.builder()
                .date(localDate)
                .title(status)
                .waterTemperature(finalWaterTemp)
                .bestTime(bestTime)
                .status(status)
                .description(description)
                .chartData(waveHeights)
                .build();
    }

    // fetching weather from API (marine-api for Portugal)
    public JsonNode fetchWeather(LocalDate date) {
        String url = String.format(
                "https://marine-api.open-meteo.com/v1/marine?latitude=39.708336&longitude=-9.124985&hourly=wave_height,sea_surface_temperature&start_date=%s&end_date=%s",
                date, date
        );

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }
}
