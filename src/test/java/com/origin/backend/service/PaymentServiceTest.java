package com.origin.backend.service;

import com.origin.backend.dto.payment.CreatePaymentRequest;
import com.origin.backend.dto.payment.PaymentResponse;
import com.origin.backend.exception.EntityNotFoundException;
import com.origin.backend.exception.PaymentFailedException;
import com.origin.backend.mapper.PaymentMapper;
import com.origin.backend.model.Booking;
import com.origin.backend.model.Payment;
import com.origin.backend.model.enums.PaymentStatus;
import com.origin.backend.repository.BookingRepository;
import com.origin.backend.repository.PaymentRepository;
import com.origin.backend.service.impl.PaymentServiceImpl;
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
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository repository;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    @DisplayName("Create payment - Success")
    void createPayment_ValidRequest_ReturnsPaymentResponse() {
        UUID idempotencyKey = UUID.randomUUID();
        CreatePaymentRequest request = TestUtil.createPaymentRequest();
        Booking booking = TestUtil.createBooking();
        booking.setTotalPrice(BigDecimal.valueOf(249.50));

        Payment payment = TestUtil.createPayment(booking);
        payment.setAmount(null);
        payment.setCardLastFour(null);

        Payment savedPayment = TestUtil.createPayment(booking);
        PaymentResponse responseDto = TestUtil.createPaymentResponse();

        when(repository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.empty());
        when(bookingRepository.findById(request.bookingId())).thenReturn(Optional.of(booking));
        when(paymentMapper.toEntity(request)).thenReturn(payment);
        when(repository.save(payment)).thenReturn(savedPayment);
        when(paymentMapper.toDto(savedPayment)).thenReturn(responseDto);

        PaymentResponse result = paymentService.createPayment(request, idempotencyKey);

        assertThat(result).isNotNull().isEqualTo(responseDto);
        assertThat(payment.getBooking()).isEqualTo(booking);
        assertThat(payment.getAmount()).isEqualByComparingTo(booking.getTotalPrice());
        assertThat(payment.getCardLastFour()).isEqualTo("0123");
        assertThat(payment.getIdempotencyKey()).isEqualTo(idempotencyKey);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        verify(repository, times(1)).save(payment);
    }

    @Test
    @DisplayName("Create payment - Existing idempotency key returns existing payment")
    void createPayment_ExistingIdempotencyKey_ReturnsExistingPayment() {
        UUID idempotencyKey = UUID.randomUUID();
        CreatePaymentRequest request = TestUtil.createPaymentRequest();
        Payment existing = TestUtil.createPayment(TestUtil.createBooking());
        PaymentResponse responseDto = TestUtil.createPaymentResponse();

        when(repository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.of(existing));
        when(paymentMapper.toDto(existing)).thenReturn(responseDto);

        PaymentResponse result = paymentService.createPayment(request, idempotencyKey);

        assertThat(result).isNotNull().isEqualTo(responseDto);
        verify(bookingRepository, never()).findById(any());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Create payment - Booking Not Found - Throws EntityNotFoundException")
    void createPayment_NonExistingBookingId_ThrowsException() {
        UUID idempotencyKey = UUID.randomUUID();
        CreatePaymentRequest request = TestUtil.createPaymentRequest();

        when(repository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.empty());
        when(bookingRepository.findById(request.bookingId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.createPayment(request, idempotencyKey))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Booking by id: " + request.bookingId() + " not found!");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Create payment - Expired Card - Throws PaymentFailedException")
    void createPayment_ExpiredCard_ThrowsException() {
        UUID idempotencyKey = UUID.randomUUID();
        CreatePaymentRequest request = new CreatePaymentRequest(
                1L, "1234 5678 9012 0123", "Kelly Slater", YearMonth.of(2000, 1), "USA");
        Booking booking = TestUtil.createBooking();

        when(repository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.empty());
        when(bookingRepository.findById(request.bookingId())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> paymentService.createPayment(request, idempotencyKey))
                .isInstanceOf(PaymentFailedException.class)
                .hasMessageContaining("Card is expired!");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Create payment - Card expiring current month - Success")
    void createPayment_CardExpiringCurrentMonth_ReturnsPaymentResponse() {
        UUID idempotencyKey = UUID.randomUUID();
        CreatePaymentRequest request = new CreatePaymentRequest(
                1L, "1234 5678 9012 0123", "Kelly Slater", YearMonth.now(), "USA");
        Booking booking = TestUtil.createBooking();
        Payment payment = TestUtil.createPayment(booking);
        PaymentResponse responseDto = TestUtil.createPaymentResponse();

        when(repository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.empty());
        when(bookingRepository.findById(request.bookingId())).thenReturn(Optional.of(booking));
        when(paymentMapper.toEntity(request)).thenReturn(payment);
        when(repository.save(payment)).thenReturn(payment);
        when(paymentMapper.toDto(payment)).thenReturn(responseDto);

        PaymentResponse result = paymentService.createPayment(request, idempotencyKey);

        assertThat(result).isNotNull().isEqualTo(responseDto);
        verify(repository, times(1)).save(payment);
    }

    @Test
    @DisplayName("Create payment - Card last four digits are taken from card number")
    void createPayment_ValidRequest_SetsCardLastFourFromCardNumber() {
        UUID idempotencyKey = UUID.randomUUID();
        CreatePaymentRequest request = new CreatePaymentRequest(
                1L, "9999 8888 7777 6543", "Kelly Slater", YearMonth.of(2030, 12), "USA");
        Booking booking = TestUtil.createBooking();
        Payment payment = TestUtil.createPayment(booking);
        PaymentResponse responseDto = TestUtil.createPaymentResponse();

        when(repository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.empty());
        when(bookingRepository.findById(request.bookingId())).thenReturn(Optional.of(booking));
        when(paymentMapper.toEntity(request)).thenReturn(payment);
        when(repository.save(payment)).thenReturn(payment);
        when(paymentMapper.toDto(payment)).thenReturn(responseDto);

        paymentService.createPayment(request, idempotencyKey);

        assertThat(payment.getCardLastFour()).isEqualTo("6543");
    }

    @Test
    @DisplayName("Get payment by ID - Success")
    void getPaymentById_ExistingId_ReturnsPaymentResponse() {
        Long id = 1L;
        Payment payment = TestUtil.createPayment(TestUtil.createBooking());
        PaymentResponse responseDto = TestUtil.createPaymentResponse();

        when(repository.findById(id)).thenReturn(Optional.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(responseDto);

        PaymentResponse result = paymentService.getPaymentById(id);

        assertThat(result).isNotNull().isEqualTo(responseDto);
    }

    @Test
    @DisplayName("Get payment by ID - Throws EntityNotFoundException")
    void getPaymentById_NonExistingId_ThrowsException() {
        Long id = 999L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.getPaymentById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Payment by id: " + id + " not found!");

        verify(paymentMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Get payments by pageable - Returns Page of Payment Responses")
    void getPaymentsByPageable_ValidPageable_ReturnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Payment payment = TestUtil.createPayment(TestUtil.createBooking());
        Page<Payment> paymentPage = new PageImpl<>(List.of(payment));
        PaymentResponse responseDto = TestUtil.createPaymentResponse();

        when(repository.findAll(pageable)).thenReturn(paymentPage);
        when(paymentMapper.toDto(payment)).thenReturn(responseDto);

        Page<PaymentResponse> result = paymentService.getPaymentsByPageable(pageable);

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(responseDto);
    }

    @Test
    @DisplayName("Get payments by pageable - No payments - Returns empty Page")
    void getPaymentsByPageable_NoPayments_ReturnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);

        when(repository.findAll(pageable)).thenReturn(Page.empty(pageable));

        Page<PaymentResponse> result = paymentService.getPaymentsByPageable(pageable);

        assertThat(result).isNotNull().isEmpty();
        verify(paymentMapper, never()).toDto(any());
    }
}
