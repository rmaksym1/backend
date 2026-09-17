package com.origin.backend.service;

import com.origin.backend.dto.message.MessageRequest;
import com.origin.backend.dto.message.MessageResponse;
import com.origin.backend.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessageService {
    MessageResponse sendMessage(MessageRequest request);

    Page<Message> getMessages(Pageable pageable);
}
