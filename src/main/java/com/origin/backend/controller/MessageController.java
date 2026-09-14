package com.origin.backend.controller;

import com.origin.backend.dto.message.MessageRequest;
import com.origin.backend.dto.message.MessageResponse;
import com.origin.backend.model.Message;
import com.origin.backend.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Message endpoints", description = "Endpoints for message management")
@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @GetMapping
    @Operation(summary = "Endpoint for getting messages by pageable")
    public Page<Message> getMessages(Pageable pageable) {
        return messageService.getMessages(pageable);
    }

    @MessageMapping("/messages")
    @SendTo("/topic/messages")
    @Operation(summary = "Endpoint for sending a message via WebSocket")
    public MessageResponse sendMessage(@Payload MessageRequest request) {
        return messageService.sendMessage(request);
    }
}
