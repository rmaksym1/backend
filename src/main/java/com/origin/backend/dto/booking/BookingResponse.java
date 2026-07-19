package com.origin.backend.dto.booking;

import java.math.BigDecimal;

public record BookingResponse(
        Long id,
        String fullName,
        String email,
        String phoneNumber,
        Integer instructorHours,
        Long packId,
        BigDecimal totalPrice
) {}
