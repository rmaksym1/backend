package com.origin.backend.service.impl;

import com.origin.backend.dto.booking.BookingResponse;
import com.origin.backend.dto.booking.CreateBookingRequest;
import com.origin.backend.exception.EntityNotFoundException;
import com.origin.backend.mapper.BookingMapper;
import com.origin.backend.model.Booking;
import com.origin.backend.model.RentalPack;
import com.origin.backend.repository.BookingRepository;
import com.origin.backend.repository.PackRepository;
import com.origin.backend.service.BookingService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final PackRepository packRepository;
    private final BookingMapper bookingMapper;

    @Transactional
    @Override
    public BookingResponse createBooking(CreateBookingRequest request) {
        Booking booking = bookingMapper.toModel(request);

        Long packId = request.packId();
        RentalPack rentalPack = packRepository.findById(packId).orElseThrow(
                () -> new EntityNotFoundException("Rental pack by id: " + packId + " not found!")
        );

        booking.setRentalPack(rentalPack);

        // calculating total booking price
        BigDecimal instructorTotal = BigDecimal.valueOf(request.instructorHours())
                .multiply(rentalPack.getInstructorHourlyPrice());

        booking.setTotalPrice(
                rentalPack.getPricePerDay().add(instructorTotal)
        );

        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponse getBookingById(Long id) {
        return bookingMapper.toDto(getBookingByIdOrThrow(id));
    }

    @Override
    public Page<BookingResponse> getAllBookings(Pageable pageable) {
        return bookingRepository.findAll(pageable)
                .map(bookingMapper::toDto);
    }

    @Transactional
    @Override
    public void deleteBookingById(Long id) {
        Booking booking = getBookingByIdOrThrow(id);

        bookingRepository.delete(booking);
    }

    private Booking getBookingByIdOrThrow(Long id) {
        return bookingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Booking by id: " + id + " not found!")
        );
    }
}
