package com.origin.backend.mapper;

import com.origin.backend.config.MapperConfig;
import com.origin.backend.dto.participant.ParticipantRequest;
import com.origin.backend.dto.participant.ParticipantResponse;
import com.origin.backend.model.Participant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface ParticipantMapper {
    @Mapping(source = "pack.id", target = "packId")
    ParticipantResponse toDto(Participant participant);

    @Mapping(source = "packId", target = "pack.id")
    Participant toEntity(ParticipantRequest request);
}
