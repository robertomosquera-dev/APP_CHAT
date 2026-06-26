package com.rb.api_chat.dto.response;

import com.rb.api_chat.model.MessageStatus;
import com.rb.api_chat.model.MessageType;

import java.time.Instant;
import java.util.UUID;

public record MessageResponse(
        UUID id,
        UUID senderId,
        UUID chatId,
        MessageType type,
        MessageStatus status,
        String content,
        String fileUrl,
        Instant sentAt

) {
}
