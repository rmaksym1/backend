package com.origin.backend.service.impl;

import com.origin.backend.dto.pack.CreatePackRequest;
import com.origin.backend.dto.pack.PackResponse;
import com.origin.backend.dto.pack.UpdatePackRequest;
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
    public PackResponse createPack(CreatePackRequest dto) {
        RentalPack rentalPack = packMapper.toModel(dto);

        return packMapper.toDto(packRepository.save(rentalPack));
    }

    @Override
    public PackResponse getPackById(Long id) {
        RentalPack pack = findPackByIdOrThrow(id);

        return packMapper.toDto(pack);
    }

    @Override
    public Page<PackResponse> getAllPacks(Pageable pageable) {
        return packRepository.findAll(pageable)
                .map(packMapper::toDto);
    }

    @Override
    public PackResponse updatePackById(Long id, UpdatePackRequest updatePackRequestDto) {
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
