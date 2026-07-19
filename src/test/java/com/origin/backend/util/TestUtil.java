package com.origin.backend.util;

import com.origin.backend.dto.booking.BookingResponse;
import com.origin.backend.dto.booking.CreateBookingRequest;
import com.origin.backend.dto.pack.CreatePackRequestDto;
import com.origin.backend.dto.pack.PackResponseDto;
import com.origin.backend.dto.pack.UpdatePackRequestDto;
import com.origin.backend.model.Booking;
import com.origin.backend.model.RentalPack;
import java.math.BigDecimal;

public class TestUtil {
    public static RentalPack createRentalPack() {
        return new RentalPack()
                .setTitle("Example pack")
                .setDescription("Example description")
                .setPricePerDay(BigDecimal.valueOf(29.99))
                .setImageUrl("https://example.jpg");
    }

    public static PackResponseDto createPackResponseDto() {
        return new PackResponseDto(3L, "Example pack", "Example desc", BigDecimal.valueOf(39.99), "https://url.jpg", BigDecimal.valueOf(20));
    }

    public static CreatePackRequestDto createPackRequestDto() {
        return new CreatePackRequestDto("Example pack", "Example desc", BigDecimal.valueOf(29.99), "https://url.jpg", BigDecimal.valueOf(20));
    }

    public static UpdatePackRequestDto createUpdatePackRequestDto() {
        return new UpdatePackRequestDto("Example pack 2", "Example desc 2", BigDecimal.valueOf(59.99), "https://url.jpg", BigDecimal.valueOf(20));
    }

    public static Booking createBooking() {
        return new Booking()
                .setFullName("Kelly Slater")
                .setEmail("kelly@gmail.com")
                .setPhoneNumber("+1-202-555-0123")
                .setTotalPrice(BigDecimal.valueOf(125))
                .setDeleted(false);
    }

    public static CreateBookingRequest createBookingRequest() {
        return new CreateBookingRequest("Kelly Slater", "kelly@gmail.com", "+1-202-555-0123", 4, 5L);
    }

    public static BookingResponse createBookingResponse() {
        return new BookingResponse(4L, "Kelly Slater", "kelly@gmail.com", "+1-202-555-0123", 4, 5L, BigDecimal.valueOf(125));
    }
}
