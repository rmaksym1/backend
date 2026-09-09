package com.origin.backend.mapper;

import com.origin.backend.config.MapperConfig;
import com.origin.backend.dto.payment.CreatePaymentRequest;
import com.origin.backend.dto.payment.PaymentResponse;
import com.origin.backend.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = {BookingMapper.class})
public interface PaymentMapper {
    @Mapping(source = "id", target = "paymentId")
    PaymentResponse toDto(Payment payment);

    @Mapping(source = "fullName", target = "cardHolderFullName")
    Payment toEntity(CreatePaymentRequest paymentRequest);
}
