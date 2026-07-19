package com.origin.backend.dto.pack;

import java.math.BigDecimal;

public record PackResponseDto(
        Long id,
        String title,
        String description,
        BigDecimal pricePerDay,
        String imageUrl,
        BigDecimal instructorHourlyPrice
) {}
