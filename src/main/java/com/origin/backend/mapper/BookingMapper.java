package com.origin.backend.mapper;

import com.origin.backend.config.MapperConfig;
import com.origin.backend.dto.booking.BookingResponse;
import com.origin.backend.dto.booking.CreateBookingRequest;
import com.origin.backend.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface BookingMapper {
    @Mapping(source = "rentalPack.id", target = "packId")
    BookingResponse toDto(Booking booking);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rentalPack", ignore = true)
    Booking toModel(CreateBookingRequest request);
}
