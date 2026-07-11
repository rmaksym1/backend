package com.origin.backend.controller;

import com.origin.backend.dto.weather.WeatherResponseDto;
import com.origin.backend.service.WeatherService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WeatherController.class)
class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WeatherService weatherService;

    @Test
    @DisplayName("GET /weather - Success with explicit date")
    void getWeather_WithValidDate_ReturnsOkAndDto() throws Exception {
        LocalDate explicitDate = LocalDate.of(2026, 7, 11);

        WeatherResponseDto mockResponse = WeatherResponseDto.builder()
                .date(explicitDate)
                .status("Good Waves")
                .waterTemperature(20)
                .chartData(List.of(1.0, 1.2))
                .build();

        Mockito.when(weatherService.getWeatherForDate(explicitDate)).thenReturn(mockResponse);

        mockMvc.perform(get("/weather")
                        .param("date", "2026-07-11")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value("2026-07-11"))
                .andExpect(jsonPath("$.status").value("Good Waves"))
                .andExpect(jsonPath("$.waterTemperature").value(20));
    }

    @Test
    @DisplayName("GET /weather - Success without date (defaults to today)")
    void getWeather_WithoutDate_DefaultsToTodayAndReturnsOk() throws Exception {
        LocalDate today = LocalDate.now();

        WeatherResponseDto mockResponse = WeatherResponseDto.builder()
                .date(today)
                .status("Calm water")
                .waterTemperature(18)
                .build();

        Mockito.when(weatherService.getWeatherForDate(Mockito.any())).thenReturn(mockResponse);

        mockMvc.perform(get("/weather")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value(today.toString()))
                .andExpect(jsonPath("$.status").value("Calm water"));
    }
}