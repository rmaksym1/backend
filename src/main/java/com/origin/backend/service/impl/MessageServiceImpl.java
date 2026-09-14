package com.origin.backend.service.impl;

import com.origin.backend.dto.message.MessageRequest;
import com.origin.backend.dto.message.MessageResponse;
import com.origin.backend.mapper.MessageMapper;
import com.origin.backend.model.Message;
import com.origin.backend.repository.MessageRepository;
import com.origin.backend.service.MessageService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    @Override
    public MessageResponse sendMessage(MessageRequest request) {
        Message message = messageMapper.toEntity(request);
        message.setTimestamp(LocalDateTime.now());

        return messageMapper.toDto(messageRepository.save(message));
    }

    @Override
    public Page<Message> getMessages(Pageable pageable) {
        return messageRepository.findAll(pageable);
    }
}
