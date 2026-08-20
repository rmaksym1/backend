package com.origin.backend.util;

import com.origin.backend.dto.booking.BookingResponse;
import com.origin.backend.dto.booking.CreateBookingRequest;
import com.origin.backend.dto.pack.CreatePackRequest;
import com.origin.backend.dto.pack.PackResponse;
import com.origin.backend.dto.pack.UpdatePackRequest;
import com.origin.backend.dto.participant.ParticipantRequest;
import com.origin.backend.dto.participant.ParticipantResponse;
import com.origin.backend.model.Booking;
import com.origin.backend.model.RentalPack;
import com.origin.backend.model.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class TestUtil {
    public static RentalPack createRentalPack() {
        return new RentalPack()
                .setTitle("Example pack")
                .setDescription("Example description")
                .setPricePerDay(BigDecimal.valueOf(29.99))
                .setImageUrl("https://example.jpg");
    }

    public static PackResponse createPackResponseDto() {
        return new PackResponse(3L, "Example pack", "Example desc", BigDecimal.valueOf(39.99), "https://url.jpg");
    }

    public static CreatePackRequest createPackRequestDto() {
        return new CreatePackRequest("Example pack", "Example desc", BigDecimal.valueOf(29.99), "https://url.jpg");
    }

    public static UpdatePackRequest createUpdatePackRequestDto() {
        return new UpdatePackRequest("Example pack 2", "Example desc 2", BigDecimal.valueOf(59.99), "https://url.jpg");
    }

    public static Booking createBooking() {
        return new Booking()
                .setRentalDate(LocalDate.now())
                .setIssuanceTime(LocalTime.of(8, 0))
                .setFullName("Kelly Slater")
                .setEmail("kelly@gmail.com")
                .setPhoneNumber("+1-202-555-0123")
                .setTotalPrice(BigDecimal.valueOf(125))
                .setDeleted(false)
                .setStatus(BookingStatus.PENDING);
    }

    public static CreateBookingRequest createBookingRequest() {
        return new CreateBookingRequest("Kelly Slater", LocalDate.now(), LocalTime.now(), "kellyslater@gmail.com", "+1-124-522-1542", List.of(new ParticipantRequest("Kelly", 5L, 2)));
    }

    public static BookingResponse createBookingResponse() {
        return new BookingResponse(4L, "Kelly Slater", LocalDate.now(), LocalTime.now(), "Kelly Slater", "kelly@gmail.com", "+1-202-555-0123", List.of(new ParticipantResponse(4L, "Kelly", 4L, 2)), BigDecimal.valueOf(125), BookingStatus.PENDING);
    }
}
