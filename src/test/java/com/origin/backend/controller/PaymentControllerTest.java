package com.origin.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.origin.backend.dto.payment.CreatePaymentRequest;
import com.origin.backend.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.YearMonth;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerTest {
    private static final String IDEMPOTENCY_KEY_HEADER = "Idempotency-Key";
    private static final String EXISTING_IDEMPOTENCY_KEY = "123e4567-e89b-12d3-a456-426614174000";

    private final Long correctBookingId = 4L;
    private final Long incorrectBookingId = 999L;
    private final Long correctPaymentId = 7L;
    private final Long incorrectPaymentId = 999L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /payment - Success")
    @Sql(scripts = "/database/cleanup-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/database/booking/add-booking-to-bookings-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void createPayment_ValidDto_ReturnsCreatedPayment() throws Exception {
        CreatePaymentRequest requestDto = TestUtil.createPaymentRequest(correctBookingId);

        mockMvc.perform(post("/payment")
                        .header(IDEMPOTENCY_KEY_HEADER, UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").exists())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.booking.id").value(correctBookingId))
                .andExpect(jsonPath("$.booking.totalPrice").value(45.96));
    }

    @Test
    @DisplayName("POST /payment - Existing Idempotency-Key - Returns existing payment")
    @Sql(scripts = "/database/cleanup-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/database/payment/add-payment-to-payments-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void createPayment_ExistingIdempotencyKey_ReturnsExistingPayment() throws Exception {
        CreatePaymentRequest requestDto = TestUtil.createPaymentRequest(correctBookingId);

        mockMvc.perform(post("/payment")
                        .header(IDEMPOTENCY_KEY_HEADER, EXISTING_IDEMPOTENCY_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value(correctPaymentId));

        mockMvc.perform(get("/payment")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    @DisplayName("POST /payment - Incorrect booking Id - Returns 404 Not Found")
    @Sql(scripts = "/database/cleanup-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void createPayment_NonExistingBookingId_ReturnsNotFound() throws Exception {
        CreatePaymentRequest requestDto = TestUtil.createPaymentRequest(incorrectBookingId);

        mockMvc.perform(post("/payment")
                        .header(IDEMPOTENCY_KEY_HEADER, UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /payment - Expired card - Returns 400 Bad Request")
    @Sql(scripts = "/database/cleanup-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/database/booking/add-booking-to-bookings-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void createPayment_ExpiredCard_ReturnsBadRequest() throws Exception {
        CreatePaymentRequest requestDto = new CreatePaymentRequest(
                correctBookingId, "1234 5678 9012 0123", "Kelly Slater", YearMonth.of(2000, 1), "USA");

        mockMvc.perform(post("/payment")
                        .header(IDEMPOTENCY_KEY_HEADER, UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /payment - Invalid card number - Returns 400 Bad Request")
    @Sql(scripts = "/database/cleanup-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/database/booking/add-booking-to-bookings-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void createPayment_InvalidCardNumber_ReturnsBadRequest() throws Exception {
        CreatePaymentRequest requestDto = new CreatePaymentRequest(
                correctBookingId, "1234", "Kelly Slater", YearMonth.of(2030, 12), "USA");

        mockMvc.perform(post("/payment")
                        .header(IDEMPOTENCY_KEY_HEADER, UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /payment - Missing Idempotency-Key header - Returns 400 Bad Request")
    @Sql(scripts = "/database/cleanup-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/database/booking/add-booking-to-bookings-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void createPayment_MissingIdempotencyKeyHeader_ReturnsBadRequest() throws Exception {
        CreatePaymentRequest requestDto = TestUtil.createPaymentRequest(correctBookingId);

        mockMvc.perform(post("/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /payment/{id} - Success")
    @Sql(scripts = "/database/cleanup-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/database/payment/add-payment-to-payments-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getPaymentById_ExistingId_ReturnsPayment() throws Exception {
        mockMvc.perform(get("/payment/{id}", correctPaymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value(correctPaymentId))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.booking.id").value(correctBookingId))
                .andExpect(jsonPath("$.booking.email").value("kelly@gmail.com"));
    }

    @Test
    @DisplayName("GET /payment/{id} - Incorrect Id - Returns 404 Not Found")
    @Sql(scripts = "/database/cleanup-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getPaymentById_NonExistingId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/payment/{id}", incorrectPaymentId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /payment - Success with pagination")
    @Sql(scripts = "/database/cleanup-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/database/payment/add-payment-to-payments-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getPaymentsByPageable_ValidPageable_ReturnsPagedPayments() throws Exception {
        mockMvc.perform(get("/payment")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].paymentId").value(correctPaymentId));
    }

    @Test
    @DisplayName("GET /payment - No payments - Returns empty page")
    @Sql(scripts = "/database/cleanup-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getPaymentsByPageable_NoPayments_ReturnsEmptyPage() throws Exception {
        mockMvc.perform(get("/payment")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }
}
