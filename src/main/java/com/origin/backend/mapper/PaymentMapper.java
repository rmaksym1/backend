package com.origin.backend.mapper;

import com.origin.backend.config.MapperConfig;
import com.origin.backend.dto.payment.CreatePaymentRequest;
import com.origin.backend.dto.payment.PaymentResponse;
import com.origin.backend.model.Payment;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    PaymentResponse toDto(Payment payment);

    Payment toEntity(CreatePaymentRequest paymentRequest);
}
