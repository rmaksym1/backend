package com.origin.backend.dto.booking;

import com.origin.backend.model.enums.BookingStatus;

public record UpdateBookingStatusRequest(
        BookingStatus status
) {}
