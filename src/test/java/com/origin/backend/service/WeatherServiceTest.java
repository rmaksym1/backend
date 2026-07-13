package com.origin.backend.service;

import com.origin.backend.dto.weather.WeatherResponseDto;
import java.io.IOException;
import java.time.LocalDate;
import com.origin.backend.service.impl.WeatherServiceImpl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;

class WeatherServiceTest {
    private MockWebServer mockWebServer;
    private WeatherServiceImpl weatherService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        weatherService = new WeatherServiceImpl(webClient);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("getWeatherForDate - Success with good waves calculation")
    void getWeatherForDate_ValidApiResponse_ReturnsCorrectDto() {
        LocalDate testDate = LocalDate.of(2026, 7, 11);

        String mockJsonResponse = "{"
                + "\"hourly\": {"
                + "  \"wave_height\": [0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, 1.1, 1.0, 0.9, 1.1, 1.2, 0.5, 0.4, 0.3, 0.2, 0.1, 0.0, 0.0, 0.0, 0.0],"
                + "  \"sea_surface_temperature\": [19.5, 19.5, 20.0, 20.0, 20.5, 20.5, 20.0, 20.0, 19.0, 19.0, 19.5, 19.5, 20.0, 20.0, 20.5, 20.5, 20.0, 20.0, 19.0, 19.0, 19.5, 19.5, 20.0, 20.0]"
                + "}"
                + "}";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(mockJsonResponse));

        WeatherResponseDto result = weatherService.getWeatherForDate(testDate);

        assertNotNull(result);
        assertEquals(testDate, result.date());
        assertEquals("Good Waves", result.status());
        assertEquals("Perfect for everyone", result.description());

        assertEquals(19, result.waterTemperature());

        assertEquals("18:00 - 21:00", result.bestTime());
        assertEquals(24, result.chartData().size());
        assertEquals(1.02, result.chartData().get(14));
    }
}