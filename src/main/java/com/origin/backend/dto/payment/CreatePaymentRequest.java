package com.origin.backend.dto.payment;

import java.time.YearMonth;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CreatePaymentRequest(
        @NotNull(message = "Booking id cannot be null!")
        Long bookingId,
        @NotBlank(message = "Card number cannot be blank!")
        @Pattern(regexp = "^\\b\\d{4}[ -]\\d{4}[ -]\\d{4}[ -]\\d{4}\\b$", message = "Card number is not valid! Must be in format: 'XXXX XXXX XXXX XXXX'")
        String cardNumber,
        @NotBlank(message = "Full name cannot be blank!")
        String fullName,
        @NotNull(message = "Expiry date cannot be null!")
        YearMonth expiryDate,
        @NotBlank(message = "Billing country cannot be blank!")
        String billingCountry
) {}
