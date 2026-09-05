package com.origin.backend.service.impl;

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
import com.origin.backend.service.PaymentService;
import java.time.YearMonth;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository repository;
    private final PaymentMapper paymentMapper;
    private final BookingRepository bookingRepository;

    @Override
    public PaymentResponse createPayment(CreatePaymentRequest request, UUID idempotencyKey) {
        Optional<Payment> existing =
                repository.findByIdempotencyKey(idempotencyKey);

        if (existing.isPresent()) {
            return paymentMapper.toDto(existing.get());
        }

        Long bookingId = request.bookingId();

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new EntityNotFoundException("Booking by id: " + bookingId + " not found!")
        );

        YearMonth expiry = YearMonth.from(request.expiryDate());

        if (expiry.isBefore(YearMonth.now())) {
            throw new PaymentFailedException("Card is expired!");
        }

        String cardNumber = request.cardNumber();

        Payment payment = paymentMapper.toEntity(request);
        payment.setBooking(booking);
        payment.setAmount(booking.getTotalPrice());
        payment.setCardLastFour(cardNumber.substring(cardNumber.length() - 4));
        payment.setIdempotencyKey(idempotencyKey);
        payment.setStatus(PaymentStatus.SUCCESS);

        return paymentMapper.toDto(repository.save(payment));
    }

    @Override
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Payment by id: " + id + " not found!")
        );

        return paymentMapper.toDto(payment);
    }

    @Override
    public Page<PaymentResponse> getPaymentsByPageable(Pageable pageable) {
        return repository.findAll(pageable)
                .map(paymentMapper::toDto);
    }
}
