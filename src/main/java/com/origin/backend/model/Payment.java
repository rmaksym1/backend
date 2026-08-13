package com.origin.backend.model;

import com.origin.backend.model.enums.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Booking booking;

    private PaymentStatus status;

    private BigDecimal amount;

    @Column(name = "card_last_four")
    private Integer cardLastFour;

    @Column(name = "card_holder_full_name")
    private String cardHolderFullName;

    @Column(name = "billing_country")
    private String billingCountry;

    @Column(name = "created_at")
    private String createdAt;
}
