package com.origin.backend.mapper;

import com.origin.backend.config.MapperConfig;
import com.origin.backend.dto.pack.CreatePackRequest;
import com.origin.backend.dto.pack.PackResponse;
import com.origin.backend.dto.pack.UpdatePackRequest;
import com.origin.backend.model.RentalPack;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = MapperConfig.class)
public interface PackMapper {
    PackResponse toDto(RentalPack rentalPack);

    @Mapping(target = "id", ignore = true)
    RentalPack toModel(CreatePackRequest createPackRequestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePack(UpdatePackRequest updatePackRequestDto,
                    @MappingTarget RentalPack rentalPack
    );
}
