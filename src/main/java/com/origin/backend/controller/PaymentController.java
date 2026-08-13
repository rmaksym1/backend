package com.origin.backend.controller;

import com.origin.backend.dto.payment.CreatePaymentRequest;
import com.origin.backend.dto.payment.PaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Payment endpoints", description = "Endpoints for payment management")
@RequestMapping("/payment")
@RestController
@RequiredArgsConstructor
public class PaymentController {

    @PostMapping
    @Operation(summary = "Endpoint for creating a payment")
    public PaymentResponse createPayment(
            @RequestHeader("Idempotency-Key") UUID idempotencyKey,
            @Valid @RequestBody CreatePaymentRequest paymentRequest
    ) {
        return null;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Endpoint for getting a payment by id")
    public PaymentResponse getPaymentById(@PathVariable Long id) {
        return null;
    }

    @GetMapping()
    @Operation(summary = "Endpoint for getting payments by pageable")
    public Page<PaymentResponse> getPaymentsByPageable(Pageable pageable) {
        return null;
    }
}
