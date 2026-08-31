package com.origin.backend.service.impl;

import com.origin.backend.dto.booking.BookingResponse;
import com.origin.backend.dto.booking.CreateBookingRequest;
import com.origin.backend.dto.booking.UpdateBookingStatusRequest;
import com.origin.backend.dto.participant.ParticipantRequest;
import com.origin.backend.exception.EntityNotFoundException;
import com.origin.backend.mapper.BookingMapper;
import com.origin.backend.mapper.ParticipantMapper;
import com.origin.backend.model.Booking;
import com.origin.backend.model.Participant;
import com.origin.backend.model.RentalPack;
import com.origin.backend.model.enums.BookingStatus;
import com.origin.backend.repository.BookingRepository;
import com.origin.backend.repository.PackRepository;
import com.origin.backend.service.BookingService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    public static final int INSTRUCTOR_HOURLY_PRICE = 20;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ParticipantMapper participantMapper;
    private final PackRepository packRepository;

    @Transactional
    @Override
    public BookingResponse createBooking(CreateBookingRequest request) {
        Booking booking = bookingMapper.toModel(request);

        int totalInstructorHours = 0;
        int totalInstructorsPrice = 0;
        BigDecimal totalPacksPrice = BigDecimal.ZERO;

        List<Participant> participants = new ArrayList<>();

        for (ParticipantRequest participantRequest : request.participants()) {
            Long packId = participantRequest.packId();
            RentalPack rentalPack = packRepository.findById(packId).orElseThrow(
                    () -> new EntityNotFoundException("Pack by id: " + packId + " not found!")
            );

            totalInstructorHours += participantRequest.instructorHours();
            totalPacksPrice = totalPacksPrice.add(rentalPack.getPricePerDay());

            Participant participant = participantMapper.toEntity(participantRequest);
            participant.setPack(rentalPack);
            participant.setBooking(booking);

            participants.add(participant);
        }

        totalInstructorsPrice = totalInstructorHours * INSTRUCTOR_HOURLY_PRICE;

        booking.setParticipants(participants);
        booking.setStatus(BookingStatus.PENDING);
        booking.setTotalPrice(totalPacksPrice.add(BigDecimal.valueOf(totalInstructorsPrice)));

        Booking saved = bookingRepository.save(booking);

        int currentYear = LocalDate.now().getYear();
        saved.setBookingId(String.format("SS-%d-%d", currentYear, saved.getId()));

        return bookingMapper.toDto(bookingRepository.save(saved));
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

    @Override
    public BookingResponse updateBookingStatus(Long id, UpdateBookingStatusRequest request) {
        Booking booking = getBookingByIdOrThrow(id);
        booking.setStatus(request.status());

        return bookingMapper.toDto(bookingRepository.save(booking));
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
