package com.origin.backend.service;

import com.origin.backend.dto.payment.CreatePaymentRequest;
import com.origin.backend.dto.payment.PaymentResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
    PaymentResponse createPayment(CreatePaymentRequest request, UUID idempotencyKey);

    PaymentResponse getPaymentById(Long id);

    Page<PaymentResponse> getPaymentsByPageable(Pageable pageable);
}
