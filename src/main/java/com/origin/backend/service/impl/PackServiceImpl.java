package com.origin.backend.service.impl;

import com.origin.backend.dto.pack.CreatePackRequestDto;
import com.origin.backend.dto.pack.PackResponseDto;
import com.origin.backend.dto.pack.UpdatePackRequestDto;
import com.origin.backend.exception.EntityNotFoundException;
import com.origin.backend.mapper.PackMapper;
import com.origin.backend.model.RentalPack;
import com.origin.backend.repository.PackRepository;
import com.origin.backend.service.PackService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PackServiceImpl implements PackService {
    private final PackRepository packRepository;
    private final PackMapper packMapper;

    @Transactional
    @Override
    public PackResponseDto createPack(CreatePackRequestDto dto) {
        RentalPack rentalPack = packMapper.toModel(dto);

        return packMapper.toDto(packRepository.save(rentalPack));
    }

    @Override
    public PackResponseDto getPackById(Long id) {
        RentalPack pack = findPackByIdOrThrow(id);

        return packMapper.toDto(pack);
    }

    @Override
    public Page<PackResponseDto> getAllPacks(Pageable pageable) {
        return packRepository.findAll(pageable)
                .map(packMapper::toDto);
    }

    @Override
    public PackResponseDto updatePackById(Long id, UpdatePackRequestDto updatePackRequestDto) {
        RentalPack pack = findPackByIdOrThrow(id);

        packMapper.updatePack(updatePackRequestDto, pack);

        return packMapper.toDto(packRepository.save(pack));
    }

    @Transactional
    @Override
    public void deletePackById(Long id) {
        RentalPack pack = findPackByIdOrThrow(id);

        packRepository.delete(pack);
    }

    private RentalPack findPackByIdOrThrow(Long id) {
        return packRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Rental pack by id: " + id + " not found!")
        );
    }
}
