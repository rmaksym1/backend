package com.origin.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.origin.backend.dto.booking.CreateBookingRequest;
import com.origin.backend.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerTest {
    private final Long correctBookingId = 4L;
    private final Long incorrectBookingId = 999L;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("POST /bookings - Success")
    @Sql(scripts = "/database/cleanup-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/database/pack/add-pack-to-packs-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void createBooking_ValidDto_ReturnsCreatedBooking() throws Exception {
        CreateBookingRequest requestDto = TestUtil.createBookingRequest();

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.fullName").value("Kelly Slater"))
                .andExpect(jsonPath("$.totalPrice").value(45.99));
    }

    @Test
    @DisplayName("GET /bookings/{id} - Success")
    @Sql(scripts = "/database/cleanup-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/database/booking/add-booking-to-bookings-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getBookingById_ExistingId_ReturnsBooking() throws Exception {
        mockMvc.perform(get("/bookings/{id}", correctBookingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(correctBookingId))
                .andExpect(jsonPath("$.fullName").value("Kelly Slater"))
                .andExpect(jsonPath("$.email").value("kelly@gmail.com"));
    }

    @Test
    @DisplayName("GET /bookings/{id} - Incorrect Id - Returns 404 Not Found")
    @Sql(scripts = "/database/cleanup-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getBookingById_NonExistingId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/bookings/{id}", incorrectBookingId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /bookings - Success with pagination")
    @Sql(scripts = "/database/cleanup-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/database/booking/add-booking-to-bookings-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAllBookings_ValidPageable_ReturnsPagedBookings() throws Exception {
        mockMvc.perform(get("/bookings")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(correctBookingId));
    }

    @Test
    @DisplayName("DELETE /bookings/{id} - Returns 204 No Content")
    @Sql(scripts = "/database/cleanup-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/database/booking/add-booking-to-bookings-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void deleteBookingById_ExistingId_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/bookings/{id}", correctBookingId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /bookings/{id} - Incorrect Id - Returns 404 Not Found")
    @Sql(scripts = "/database/cleanup-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void deleteBookingById_InvalidId_ReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/bookings/{id}", incorrectBookingId))
                .andExpect(status().isNotFound());
    }
}