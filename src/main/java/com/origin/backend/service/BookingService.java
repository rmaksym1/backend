package com.origin.backend.service;

import com.origin.backend.dto.booking.BookingResponse;
import com.origin.backend.dto.booking.CreateBookingRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingService {
    BookingResponse createBooking(CreateBookingRequest request);

    BookingResponse getBookingById(Long id);

    Page<BookingResponse> getAllBookings(Pageable pageable);

    void deleteBookingById(Long id);
}
