package com.origin.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.origin.backend.dto.pack.CreatePackRequestDto;
import com.origin.backend.dto.pack.UpdatePackRequestDto;
import com.origin.backend.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PackControllerTest {
    Long correctPackId = 5L;
    Long incorrectPackId = 999L;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("POST /packs - Success")
    @Sql(scripts = "/database/cleanup-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void createPack_ValidDto_ReturnsCreatedPack() throws Exception {
        CreatePackRequestDto requestDto = TestUtil.createPackRequestDto();

        mockMvc.perform(post("/packs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Example pack"))
                .andExpect(jsonPath("$.pricePerDay").value(29.99));
    }

    @Test
    @DisplayName("GET /packs/{id} - Success")
    @Sql(scripts = "/database/cleanup-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/database/pack/add-pack-to-packs-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getPackById_ExistingId_ReturnsPack() throws Exception {
        mockMvc.perform(get("/packs/{id}", correctPackId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(correctPackId))
                .andExpect(jsonPath("$.title").value("Example pack"));
    }

    @Test
    @DisplayName("GET /packs/{id} - Inccorect Id - Returns 404")
    @Sql(scripts = "/database/cleanup-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/database/pack/add-pack-to-packs-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getPackById_NonExistingId_ReturnsPack() throws Exception {
        mockMvc.perform(get("/packs/{id}", incorrectPackId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /packs - Success with pagination")
    @Sql(scripts = "/database/cleanup-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/database/pack/add-pack-to-packs-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAllPacks_ValidPageable_ReturnsPagedPacks() throws Exception {
        mockMvc.perform(get("/packs")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(5L));
    }

    @Test
    @DisplayName("PATCH /packs/{id} - Success")
    @Sql(scripts = "/database/cleanup-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/database/pack/add-pack-to-packs-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void updatePackById_ValidDto_ReturnsUpdatedPack() throws Exception {
        UpdatePackRequestDto updateDto = TestUtil.createUpdatePackRequestDto();

        mockMvc.perform(patch("/packs/{id}", correctPackId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Example pack 2"));
    }

    @Test
    @DisplayName("PATCH /packs/{id} - Invalid Id - Returns 404 Not Found")
    @Sql(scripts = "/database/cleanup-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/database/pack/add-pack-to-packs-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void updatePackByInvalidId_ValidDto_ReturnsNotFound() throws Exception {
        UpdatePackRequestDto updateDto = TestUtil.createUpdatePackRequestDto();

        mockMvc.perform(patch("/packs/{id}", incorrectPackId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /packs/{id} - Returns 204 No Content")
    @Sql(scripts = "/database/cleanup-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/database/pack/add-pack-to-packs-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void deletePackById_ExistingId_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/packs/{id}", correctPackId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /packs/{id} - Incorrect Id - Returns 404 Not found")
    @Sql(scripts = "/database/cleanup-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/database/pack/add-pack-to-packs-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void deletePackById_InvalidId_ReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/packs/{id}", incorrectPackId))
                .andExpect(status().isNotFound());
    }
}