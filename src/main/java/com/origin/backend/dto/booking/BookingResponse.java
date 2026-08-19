package com.origin.backend.dto.booking;

import com.origin.backend.dto.participant.ParticipantResponse;
import com.origin.backend.model.enums.BookingStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record BookingResponse(
        Long id,
        String bookingId,
        LocalDate rentalDate,
        LocalTime issuanceTime,
        String fullName,
        String email,
        String phoneNumber,
        List<ParticipantResponse> participants,
        BigDecimal totalPrice,
        BookingStatus status
) {}
