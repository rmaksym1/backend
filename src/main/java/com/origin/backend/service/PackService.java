package com.origin.backend.service;

import com.origin.backend.dto.pack.CreatePackRequestDto;
import com.origin.backend.dto.pack.PackResponseDto;
import com.origin.backend.dto.pack.UpdatePackRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PackService {
    PackResponseDto createPack(CreatePackRequestDto dto);

    PackResponseDto getPackById(Long id);

    Page<PackResponseDto> getAllPacks(Pageable pageable);

    PackResponseDto updatePackById(Long id, UpdatePackRequestDto updatePackRequestDto);

    void deletePackById(Long id);
}
