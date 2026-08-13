package com.origin.backend.dto.payment;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePaymentRequest(
        @NotNull(message = "Booking id cannot be null!")
        Long bookingId,
        @NotBlank(message = "Card number cannot be blank!")
        String cardNumber,
        @NotBlank(message = "Full name cannot be blank!")
        String fullName,
        @NotNull(message = "Expiry date cannot be null!")
        LocalDate expiryDate,
        @NotBlank(message = "Billing country cannot be blank!")
        String billingCountry
) {}
