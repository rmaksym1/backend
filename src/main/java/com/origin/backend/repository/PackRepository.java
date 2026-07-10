package com.origin.backend.repository;

import com.origin.backend.model.RentalPack;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackRepository extends JpaRepository<RentalPack, Long> {
}
