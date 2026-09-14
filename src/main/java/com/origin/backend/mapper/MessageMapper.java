package com.origin.backend.mapper;

import com.origin.backend.config.MapperConfig;
import com.origin.backend.dto.message.MessageRequest;
import com.origin.backend.dto.message.MessageResponse;
import com.origin.backend.model.Message;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface MessageMapper {
    MessageResponse toDto(Message message);

    Message toEntity(MessageRequest request);
}
