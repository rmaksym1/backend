package com.origin.backend.service;

import com.origin.backend.dto.pack.CreatePackRequestDto;
import com.origin.backend.dto.pack.PackResponseDto;
import com.origin.backend.dto.pack.UpdatePackRequestDto;
import com.origin.backend.exception.EntityNotFoundException;
import com.origin.backend.mapper.PackMapper;
import com.origin.backend.model.RentalPack;
import com.origin.backend.repository.PackRepository;
import com.origin.backend.service.impl.PackServiceImpl;
import com.origin.backend.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PackServiceTest {

    @Mock
    private PackRepository packRepository;

    @Mock
    private PackMapper packMapper;

    @InjectMocks
    private PackServiceImpl packService;

    @Test
    @DisplayName("Create pack - Success")
    void createPack_ValidDto_ReturnsResponseDto() {
        CreatePackRequestDto requestDto = TestUtil.createPackRequestDto();
        RentalPack model = TestUtil.createRentalPack();
        RentalPack savedModel = TestUtil.createRentalPack();
        PackResponseDto responseDto = TestUtil.createPackResponseDto();

        when(packMapper.toModel(requestDto)).thenReturn(model);
        when(packRepository.save(model)).thenReturn(savedModel);
        when(packMapper.toDto(savedModel)).thenReturn(responseDto);

        PackResponseDto result = packService.createPack(requestDto);

        assertThat(result).isNotNull().isEqualTo(responseDto);
        verify(packRepository, times(1)).save(model);
    }

    @Test
    @DisplayName("Get pack by ID - Success")
    void getPackById_ExistingId_ReturnsResponseDto() {
        Long id = 1L;
        RentalPack pack = TestUtil.createRentalPack();
        PackResponseDto responseDto = TestUtil.createPackResponseDto();

        when(packRepository.findById(id)).thenReturn(Optional.of(pack));
        when(packMapper.toDto(pack)).thenReturn(responseDto);

        PackResponseDto result = packService.getPackById(id);

        assertThat(result).isNotNull().isEqualTo(responseDto);
    }

    @Test
    @DisplayName("Get pack by ID - Throws EntityNotFoundException")
    void getPackById_NonExistingId_ThrowsException() {
        Long id = 999L;
        when(packRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> packService.getPackById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Rental pack by id: " + id + " not found!");

        verify(packMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Get all packs - Returns Page of Response Dtos")
    void getAllPacks_ValidPageable_ReturnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        RentalPack pack = TestUtil.createRentalPack();
        Page<RentalPack> packPage = new PageImpl<>(List.of(pack));
        PackResponseDto responseDto = TestUtil.createPackResponseDto();

        when(packRepository.findAll(pageable)).thenReturn(packPage);
        when(packMapper.toDto(pack)).thenReturn(responseDto);

        Page<PackResponseDto> result = packService.getAllPacks(pageable);

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(responseDto);
    }

    @Test
    @DisplayName("Update pack by ID - Success")
    void updatePackById_ExistingId_ReturnsUpdatedDto() {
        Long id = 1L;
        UpdatePackRequestDto updateDto = TestUtil.createUpdatePackRequestDto();
        RentalPack pack = TestUtil.createRentalPack();
        PackResponseDto responseDto = TestUtil.createPackResponseDto();

        when(packRepository.findById(id)).thenReturn(Optional.of(pack));
        doNothing().when(packMapper).updatePack(updateDto, pack);
        when(packMapper.toDto(pack)).thenReturn(responseDto);

        PackResponseDto result = packService.updatePackById(id, updateDto);

        assertThat(result).isNotNull().isEqualTo(responseDto);
        verify(packMapper, times(1)).updatePack(updateDto, pack);
    }

    @Test
    @DisplayName("Delete pack by ID - Success")
    void deletePackById_ExistingId_DeletesPack() {
        Long id = 1L;
        RentalPack pack = TestUtil.createRentalPack();

        when(packRepository.findById(id)).thenReturn(Optional.of(pack));
        doNothing().when(packRepository).delete(pack);

        packService.deletePackById(id);

        verify(packRepository, times(1)).delete(pack);
    }

    @Test
    @DisplayName("Delete pack by Incorrect ID - Not Found")
    void deletePackById_NonExistingId_DeletesPack() {
        Long id = 999L;

        when(packRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> packService.deletePackById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Rental pack by id: " + id + " not found!");

        verifyNoMoreInteractions(packRepository);
    }
}