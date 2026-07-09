package com.origin.backend.service;

import com.origin.backend.dto.WeatherResponseDto;
import java.time.LocalDate;

public interface WeatherService {
    WeatherResponseDto getWeatherForDate(LocalDate localDate);
}
