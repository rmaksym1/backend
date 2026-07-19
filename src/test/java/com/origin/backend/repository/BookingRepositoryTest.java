package com.origin.backend.repository;

import com.origin.backend.model.Booking;
import com.origin.backend.model.RentalPack;
import com.origin.backend.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Sql(scripts = "/database/cleanup-db.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PackRepository packRepository;

    @Test
    @DisplayName("Should successfully save and find booking by ID")
    void shouldSaveAndFindBookingById() {
        RentalPack savedPack = packRepository.save(TestUtil.createRentalPack());

        Booking booking = TestUtil.createBooking();
        booking.setRentalPack(savedPack);
        Booking savedBooking = bookingRepository.save(booking);

        Optional<Booking> foundBookingOpt = bookingRepository.findById(savedBooking.getId());

        assertThat(foundBookingOpt).isPresent();
        Booking foundBooking = foundBookingOpt.get();

        assertThat(foundBooking.getId()).isNotNull();
        assertThat(foundBooking.getFullName()).isEqualTo("Kelly Slater");
        assertThat(foundBooking.getTotalPrice()).isEqualTo(BigDecimal.valueOf(125));
        assertThat(foundBooking.getRentalPack().getId()).isEqualTo(savedPack.getId());
    }

    @Test
    @DisplayName("Should return all bookings")
    void shouldFindAllBookings() {
        RentalPack savedPack = packRepository.save(TestUtil.createRentalPack());

        Booking booking1 = TestUtil.createBooking();
        booking1.setRentalPack(savedPack);
        bookingRepository.save(booking1);

        Booking booking2 = TestUtil.createBooking();
        booking2.setFullName("Laird Hamilton");
        booking2.setEmail("laird@gmail.com");
        booking2.setRentalPack(savedPack);
        bookingRepository.save(booking2);

        List<Booking> bookings = bookingRepository.findAll();

        assertThat(bookings).hasSize(2);
    }

    @Test
    @DisplayName("Should delete booking successfully")
    void shouldDeleteBooking() {
        RentalPack savedPack = packRepository.save(TestUtil.createRentalPack());

        Booking booking = TestUtil.createBooking();
        booking.setRentalPack(savedPack);
        Booking savedBooking = bookingRepository.save(booking);

        bookingRepository.deleteById(savedBooking.getId());
        Optional<Booking> deletedBookingOpt = bookingRepository.findById(savedBooking.getId());

        assertThat(deletedBookingOpt).isEmpty();
    }
}