package com.origin.backend.dto.message;

import java.time.LocalDateTime;

public record MessageResponse(
        Long id,
        String sender,
        String content,
        LocalDateTime timestamp
) {}
