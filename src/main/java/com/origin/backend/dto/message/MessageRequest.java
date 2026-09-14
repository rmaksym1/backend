package com.origin.backend.dto.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MessageRequest(
        @NotBlank(message = "Sender cannot be blank!")
        @Size(max = 255, message = "Sender cannot exceed 255 characters!")
        String sender,
        @NotBlank(message = "Content cannot be blank!")
        @Size(max = 4096, message = "Content cannot exceed 4096 characters!")
        String content
) {}
