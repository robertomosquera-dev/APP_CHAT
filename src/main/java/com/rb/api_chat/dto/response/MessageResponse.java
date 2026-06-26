package com.rb.api_chat.dto.response;

import com.rb.api_chat.model.MessageStatus;
import com.rb.api_chat.model.MessageType;

import java.time.Instant;

public record MessageResponse(
        String messageId,
        MessageType type,
        MessageStatus status,
        String content,
        String url,
        Instant createdAt,
        ChatResponse chat
) {
}
