package com.origin.backend.mapper;

import com.origin.backend.config.MapperConfig;
import com.origin.backend.dto.pack.CreatePackRequestDto;
import com.origin.backend.dto.pack.PackResponseDto;
import com.origin.backend.dto.pack.UpdatePackRequestDto;
import com.origin.backend.model.RentalPack;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = MapperConfig.class)
public interface PackMapper {
    PackResponseDto toDto(RentalPack rentalPack);

    @Mapping(target = "id", ignore = true)
    RentalPack toModel(CreatePackRequestDto createPackRequestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePack(UpdatePackRequestDto updatePackRequestDto,
                    @MappingTarget RentalPack rentalPack
    );
}
