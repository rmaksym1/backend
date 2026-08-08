package com.origin.backend.service;

import com.origin.backend.dto.pack.CreatePackRequest;
import com.origin.backend.dto.pack.PackResponse;
import com.origin.backend.dto.pack.UpdatePackRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PackService {
    PackResponse createPack(CreatePackRequest dto);

    PackResponse getPackById(Long id);

    Page<PackResponse> getAllPacks(Pageable pageable);

    PackResponse updatePackById(Long id, UpdatePackRequest updatePackRequestDto);

    void deletePackById(Long id);
}
