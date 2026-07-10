package com.origin.backend.controller;

import com.origin.backend.dto.pack.CreatePackRequestDto;
import com.origin.backend.dto.pack.PackResponseDto;
import com.origin.backend.dto.pack.UpdatePackRequestDto;
import com.origin.backend.service.PackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Pack endpoints", description = "Endpoints for surfing packs management")
@RestController
@RequestMapping("/packs")
@RequiredArgsConstructor
public class PackController {
    private final PackService packService;

    @PostMapping
    @Operation(summary = "Endpoint for creating a pack")
    public PackResponseDto createPack(@RequestBody @Valid CreatePackRequestDto dto) {
        return packService.createPack(dto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Endpoint for getting a pack by id")
    public PackResponseDto getPackById(@PathVariable Long id) {
        return packService.getPackById(id);
    }

    @GetMapping
    @Operation(summary = "Endpoint for getting all packs by params")
    public Page<PackResponseDto> getAllPacks(Pageable pageable) {
        return packService.getAllPacks(pageable);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Endpoint for updating a pack by id")
    public PackResponseDto updatePackById(
            @PathVariable Long id,
            @RequestBody @Valid UpdatePackRequestDto updatePackRequestDto
    ) {
        return packService.updatePackById(id, updatePackRequestDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Endpoint for soft-deleting a pack by id")
    public ResponseEntity<Void> deletePackById(@PathVariable Long id) {
        packService.deletePackById(id);
        return ResponseEntity.noContent().build();
    }
}
