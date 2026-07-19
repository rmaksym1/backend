package com.origin.backend.dto.pack;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CreatePackRequestDto(
        @NotBlank(message = "Title cannot be blank!")
        @Size(message = "Title must be between 1 and 255 characters long!", min = 1, max = 255)
        String title,
        @Size(message = "Description must be not more than 1024 characters long!", max = 1024)
        String description,
        @NotNull(message = "Price per day must not be null!")
        @DecimalMin(value = "0.01", message = "Price per day must be greater than 0!")
        BigDecimal pricePerDay,
        @Size(message = "Image url must be not more than 512 characters long!", max = 512)
        String imageUrl,
        @NotNull(message = "Instructor price per hour cannot be null!")
        @DecimalMin(value = "0.01", message = "Instructor price per hour must be greater than 0!")
        BigDecimal instructorHourlyPrice
) { }
