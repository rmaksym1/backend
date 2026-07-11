package com.origin.backend.repository;

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
class PackRepositoryTest {

    @Autowired
    private PackRepository packRepository;

    @Test
    @DisplayName("Should successfully save and find pack by ID")
    void shouldSaveAndFindPackById() {
        RentalPack savedPack = packRepository.save(TestUtil.createRentalPack());

        Optional<RentalPack> foundPackOpt = packRepository.findById(savedPack.getId());

        assertThat(foundPackOpt).isPresent();
        RentalPack foundPack = foundPackOpt.get();

        assertThat(foundPack.getId()).isNotNull();
        assertThat(foundPack.getTitle()).isEqualTo("Example pack");
        assertThat(foundPack.getPricePerDay()).isEqualTo(BigDecimal.valueOf(29.99));
    }

    @Test
    @DisplayName("Should return all packs")
    void shouldFindAllPacks() {
        RentalPack savedPack1 = TestUtil.createRentalPack();
        savedPack1.setTitle("Example pack 2");
        savedPack1.setDescription("Example description 2");
        packRepository.save(savedPack1);

        RentalPack anotherPack = TestUtil.createRentalPack();
        packRepository.save(anotherPack);

        List<RentalPack> packs = packRepository.findAll();

        assertThat(packs).hasSize(2);
    }

    @Test
    @DisplayName("Should delete pack successfully")
    void shouldDeletePack() {
        RentalPack rentalPack = packRepository.save(TestUtil.createRentalPack());

        packRepository.deleteById(rentalPack.getId());
        Optional<RentalPack> deletedPackOpt = packRepository.findById(rentalPack.getId());

        assertThat(deletedPackOpt).isEmpty();
    }
}