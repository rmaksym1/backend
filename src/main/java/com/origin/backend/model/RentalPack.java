package com.origin.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@SQLDelete(sql = "UPDATE packs SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted=false")
@Getter
@Setter
@Table(name = "packs")
public class RentalPack {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    private String description;

    private BigDecimal pricePerDay;

    private String imageUrl;

    @Column(name = "is_deleted")
    private boolean isDeleted;
}
