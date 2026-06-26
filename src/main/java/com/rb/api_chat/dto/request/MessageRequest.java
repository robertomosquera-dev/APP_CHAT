package com.rb.api_chat.dto.request;

import com.rb.api_chat.model.MessageType;

import java.util.UUID;

public record MessageRequest(
    UUID senderId,
    UUID receiverId,
    UUID chatId,
    String content,
    MessageType type
) {
}
