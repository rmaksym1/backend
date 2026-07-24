package com.origin.backend.service;

import com.origin.backend.dto.weather.DailyWeatherResponseDto;
import com.origin.backend.dto.weather.WeatherResponseDto;
import java.time.LocalDate;
import java.util.List;

public interface WeatherService {
    WeatherResponseDto getWeatherForDate(LocalDate localDate);

    List<DailyWeatherResponseDto> getWeatherByDatesBetween(LocalDate from, LocalDate to);
}
