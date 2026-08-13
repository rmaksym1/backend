package com.origin.backend.model;

import com.origin.backend.model.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Booking booking;

    private PaymentStatus status;

    private BigDecimal amount;

    @Column(name = "card_last_four")
    private String cardLastFour;

    @Column(name = "card_holder_full_name")
    private String cardHolderFullName;

    @Column(name = "billing_country")
    private String billingCountry;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;
}
