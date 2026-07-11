package com.origin.backend.service;

import com.origin.backend.dto.weather.WeatherResponseDto;
import java.time.LocalDate;

public interface WeatherService {
    WeatherResponseDto getWeatherForDate(LocalDate localDate);
}
