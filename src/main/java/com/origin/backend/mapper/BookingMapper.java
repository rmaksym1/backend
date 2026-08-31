package com.origin.backend.mapper;

import com.origin.backend.config.MapperConfig;
import com.origin.backend.dto.booking.BookingResponse;
import com.origin.backend.dto.booking.CreateBookingRequest;
import com.origin.backend.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = {ParticipantMapper.class})
public interface BookingMapper {
    BookingResponse toDto(Booking booking);

    @Mapping(target = "id", ignore = true)
    Booking toModel(CreateBookingRequest request);
}
