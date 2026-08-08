package com.origin.backend.service;

import com.origin.backend.dto.weather.DailyWeatherResponse;
import com.origin.backend.dto.weather.WeatherResponse;
import java.time.LocalDate;
import java.util.List;

public interface WeatherService {
    WeatherResponse getWeatherForDate(LocalDate localDate);

    List<DailyWeatherResponse> getWeatherByDatesBetween(LocalDate from, LocalDate to);
}
