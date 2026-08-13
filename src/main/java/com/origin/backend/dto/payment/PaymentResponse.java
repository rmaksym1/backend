package com.origin.backend.dto.payment;

import com.origin.backend.dto.booking.BookingResponse;
import com.origin.backend.model.enums.PaymentStatus;

public record PaymentResponse(
        Long paymentId,
        BookingResponse bookingResponse,
        PaymentStatus status
) {}
