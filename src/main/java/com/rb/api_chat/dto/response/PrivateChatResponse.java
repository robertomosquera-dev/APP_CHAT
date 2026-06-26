package com.rb.api_chat.dto.response;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record PrivateChatResponse (
        UUID chatId,
        UUID senderId,
        UUID receiverId,
        String name,
        String photoUrl,
        Instant createdAt
) implements ChatResponse{}
