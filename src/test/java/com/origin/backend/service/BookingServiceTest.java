package com.origin.backend.service;

import com.origin.backend.dto.booking.BookingResponse;
import com.origin.backend.dto.booking.CreateBookingRequest;
import com.origin.backend.exception.EntityNotFoundException;
import com.origin.backend.mapper.BookingMapper;
import com.origin.backend.mapper.ParticipantMapper;
import com.origin.backend.model.Booking;
import com.origin.backend.model.Participant;
import com.origin.backend.model.RentalPack;
import com.origin.backend.repository.BookingRepository;
import com.origin.backend.repository.PackRepository;
import com.origin.backend.service.impl.BookingServiceImpl;
import com.origin.backend.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ParticipantMapper participantMapper;

    @Mock
    private PackRepository packRepository;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    @DisplayName("Create booking - Success")
    void createBooking_ValidRequest_ReturnsBookingResponse() {
        CreateBookingRequest request = TestUtil.createBookingRequest();
        Booking booking = TestUtil.createBooking();
        RentalPack rentalPack = TestUtil.createRentalPack();
        rentalPack.setPricePerDay(BigDecimal.valueOf(29.99));

        Booking savedBookingFirstTime = TestUtil.createBooking();
        savedBookingFirstTime.setId(1L);

        Booking savedBookingFinal = TestUtil.createBooking();
        savedBookingFinal.setId(1L);
        savedBookingFinal.setBookingId("SS-2026-1");
        savedBookingFinal.setTotalPrice(BigDecimal.valueOf(49.99));

        BookingResponse responseDto = TestUtil.createBookingResponse();

        when(bookingMapper.toModel(request)).thenReturn(booking);

        Long expectedPackId = request.participants().get(0).packId();
        when(packRepository.findById(expectedPackId)).thenReturn(Optional.of(rentalPack));

        when(participantMapper.toEntity(any())).thenReturn(new Participant());

        when(bookingRepository.save(booking)).thenReturn(savedBookingFirstTime);
        when(bookingRepository.save(savedBookingFirstTime)).thenReturn(savedBookingFinal);
        when(bookingMapper.toDto(savedBookingFinal)).thenReturn(responseDto);

        BookingResponse result = bookingService.createBooking(request);

        assertThat(result).isNotNull().isEqualTo(responseDto);

        verify(packRepository, times(1)).findById(expectedPackId);
        verify(bookingRepository, times(2)).save(any(Booking.class));
        verify(bookingMapper, times(1)).toModel(request);
        verify(bookingMapper, times(1)).toDto(savedBookingFinal);
    }

    @Test
    @DisplayName("Create booking - Pack Not Found - Throws EntityNotFoundException")
    void createBooking_NonExistingPackId_ThrowsException() {
        CreateBookingRequest request = TestUtil.createBookingRequest();
        Booking booking = TestUtil.createBooking();

        when(bookingMapper.toModel(request)).thenReturn(booking);

        assertThatThrownBy(() -> bookingService.createBooking(request))
                .isInstanceOf(EntityNotFoundException.class);

        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Get booking by ID - Success")
    void getBookingById_ExistingId_ReturnsBookingResponse() {
        Long id = 1L;
        Booking booking = TestUtil.createBooking();
        BookingResponse responseDto = TestUtil.createBookingResponse();

        when(bookingRepository.findById(id)).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(responseDto);

        BookingResponse result = bookingService.getBookingById(id);

        assertThat(result).isNotNull().isEqualTo(responseDto);
    }

    @Test
    @DisplayName("Get booking by ID - Throws EntityNotFoundException")
    void getBookingById_NonExistingId_ThrowsException() {
        Long id = 999L;
        when(bookingRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getBookingById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Booking by id: " + id + " not found!");

        verify(bookingMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Get all bookings - Returns Page of Booking Responses")
    void getAllBookings_ValidPageable_ReturnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Booking booking = TestUtil.createBooking();
        Page<Booking> bookingPage = new PageImpl<>(List.of(booking));
        BookingResponse responseDto = TestUtil.createBookingResponse();

        when(bookingRepository.findAll(pageable)).thenReturn(bookingPage);
        when(bookingMapper.toDto(booking)).thenReturn(responseDto);

        Page<BookingResponse> result = bookingService.getAllBookings(pageable);

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(responseDto);
    }

    @Test
    @DisplayName("Delete booking by ID - Success")
    void deleteBookingById_ExistingId_DeletesBooking() {
        Long id = 1L;
        Booking booking = TestUtil.createBooking();

        when(bookingRepository.findById(id)).thenReturn(Optional.of(booking));
        doNothing().when(bookingRepository).delete(booking);

        bookingService.deleteBookingById(id);

        verify(bookingRepository, times(1)).delete(booking);
    }

    @Test
    @DisplayName("Delete booking by Incorrect ID - Not Found")
    void deleteBookingById_NonExistingId_ThrowsException() {
        Long id = 999L;
        when(bookingRepository.findById(id)).thenReturn(Optional.empty());
        
        assertThatThrownBy(() -> bookingService.deleteBookingById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Booking by id: " + id + " not found!");

        verifyNoMoreInteractions(bookingRepository);
    }
}