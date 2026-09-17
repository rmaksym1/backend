package com.origin.backend.repository;

import com.origin.backend.model.Booking;
import com.origin.backend.model.Payment;
import com.origin.backend.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.cache.autoconfigure.CacheAutoConfiguration;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@ImportAutoConfiguration(CacheAutoConfiguration.class)
@Sql(scripts = "/database/cleanup-db.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    @DisplayName("Should successfully save and find payment by ID")
    void shouldSaveAndFindPaymentById() {
        Booking savedBooking = bookingRepository.save(TestUtil.createBooking());

        Payment savedPayment = paymentRepository.save(TestUtil.createPayment(savedBooking));

        Optional<Payment> foundPaymentOpt = paymentRepository.findById(savedPayment.getId());

        assertThat(foundPaymentOpt).isPresent();
        Payment foundPayment = foundPaymentOpt.get();

        assertThat(foundPayment.getId()).isNotNull();
        assertThat(foundPayment.getBooking().getId()).isEqualTo(savedBooking.getId());
        assertThat(foundPayment.getCardHolderFullName()).isEqualTo("Kelly Slater");
        assertThat(foundPayment.getCardLastFour()).isEqualTo("0123");
        assertThat(foundPayment.getBillingCountry()).isEqualTo("USA");
        assertThat(foundPayment.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(125));
        assertThat(foundPayment.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should find payment by idempotency key")
    void shouldFindPaymentByIdempotencyKey() {
        Booking savedBooking = bookingRepository.save(TestUtil.createBooking());

        Payment savedPayment = paymentRepository.save(TestUtil.createPayment(savedBooking));
        UUID idempotencyKey = savedPayment.getIdempotencyKey();

        Optional<Payment> foundPaymentOpt = paymentRepository.findByIdempotencyKey(idempotencyKey);

        assertThat(foundPaymentOpt).isPresent();
        assertThat(foundPaymentOpt.get().getId()).isEqualTo(savedPayment.getId());
        assertThat(foundPaymentOpt.get().getIdempotencyKey()).isEqualTo(idempotencyKey);
    }

    @Test
    @DisplayName("Should return empty for unknown idempotency key")
    void shouldReturnEmptyForUnknownIdempotencyKey() {
        Optional<Payment> foundPaymentOpt = paymentRepository.findByIdempotencyKey(UUID.randomUUID());

        assertThat(foundPaymentOpt).isEmpty();
    }

    @Test
    @DisplayName("Should return all payments")
    void shouldFindAllPayments() {
        Booking savedBooking = bookingRepository.save(TestUtil.createBooking());

        paymentRepository.save(TestUtil.createPayment(savedBooking));
        paymentRepository.save(TestUtil.createPayment(savedBooking));

        List<Payment> payments = paymentRepository.findAll();

        assertThat(payments).hasSize(2);
    }

    @Test
    @DisplayName("Should delete payment successfully")
    void shouldDeletePayment() {
        Booking savedBooking = bookingRepository.save(TestUtil.createBooking());

        Payment savedPayment = paymentRepository.save(TestUtil.createPayment(savedBooking));

        paymentRepository.deleteById(savedPayment.getId());
        Optional<Payment> deletedPaymentOpt = paymentRepository.findById(savedPayment.getId());

        assertThat(deletedPaymentOpt).isEmpty();
    }
}
