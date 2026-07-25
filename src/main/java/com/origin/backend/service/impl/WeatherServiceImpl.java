package com.origin.backend.service.impl;

import com.origin.backend.dto.weather.DailyWeatherResponseDto;
import com.origin.backend.dto.weather.WeatherResponseDto;
import com.origin.backend.service.WeatherService;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.JsonNode;

@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {

    private final WebClient webClient;

    @Override
    @Cacheable(value = "weather_daily",
            key = "#localDate != null ? #localDate : T(java.time.LocalDate).now()"
    )
    public WeatherResponseDto getWeatherForDate(LocalDate localDate) {
        if (localDate == null) {
            localDate = LocalDate.now();
        }

        checkDates(LocalDate.now(), localDate);

        JsonNode response = fetchWeather(localDate, localDate);
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
        double maxWaveHeight = waveHeights.stream()
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
        WaveInfo waveInfo = getStatusAndDescriptionByWaveHeight(maxWaveHeight);

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
                .waterTemperature(finalWaterTemp)
                .bestTime(bestTime)
                .status(waveInfo.status)
                .description(waveInfo.description)
                .chartData(waveHeights)
                .build();
    }

    @Cacheable(value = "weather_range", key = "#from.toString() + '_' + #to.toString()")
    public List<DailyWeatherResponseDto> getWeatherByDatesBetween(LocalDate from, LocalDate to) {
        checkDates(from, to);

        if (from.isAfter(to)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "'from' date cannot be after 'to' date"
            );
        }

        long days = ChronoUnit.DAYS.between(from, to) + 1;

        List<DailyWeatherResponseDto> result = new ArrayList<>();
        JsonNode response = fetchWeather(from, to);
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

        int hourlyCounter = 0;
        LocalDate currentDay = from;

        for (int i = 0; i < days; i++) {
            int hoursInDay = 24;

            if (hourlyCounter + hoursInDay > waveHeights.size()) {
                break;
            }

            // limiting the arrays by subList for calculating data for current day in cycle
            List<Double> waveHeightsSubList =
                    waveHeights.subList(hourlyCounter, hourlyCounter + hoursInDay);

            // calculating max wave height
            double maxWaveHeight = waveHeightsSubList.stream()
                    .mapToDouble(Double::doubleValue)
                    .max()
                    .orElse(0.0);

            hourlyCounter += hoursInDay;

            WaveInfo waveInfo = getStatusAndDescriptionByWaveHeight(maxWaveHeight);

            result.add(new DailyWeatherResponseDto(currentDay,
                    waveInfo.status,
                    waveInfo.description));

            currentDay = currentDay.plusDays(1);
        }

        return result;
    }

    private void checkDates(LocalDate from, LocalDate to) {
        long days = ChronoUnit.DAYS.between(from, to) + 1;

        if (days > 8) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Maximum valid forecast range is 8 days"
            );
        }
    }

    // fetching weather from API (marine-api for Portugal)
    private JsonNode fetchWeather(LocalDate from, LocalDate to) {
        String url = String.format(
                "https://marine-api.open-meteo.com/v1/marine?latitude=39.708336&longitude=-9.124985&hourly=wave_height,sea_surface_temperature&start_date=%s&end_date=%s",
                from, to
        );

        return Objects.requireNonNull(webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block());
    }

    private record WaveInfo(String status, String description) {}

    private WaveInfo getStatusAndDescriptionByWaveHeight(double maxWaveHeight) {
        if (maxWaveHeight > 1.5) {
            return new WaveInfo("Huge Swell", "Pro surfers only");
        } else if (maxWaveHeight > 0.7) {
            return new WaveInfo("Good Waves", "Perfect for everyone");
        } else {
            return new WaveInfo("Calm water", "Good for beginners");
        }
    }
}
