package com.rb.api_chat.dto.response;

import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
public record GroupChatResponse(
        UUID chatId,
        String name,
        List<UUID> usersId,
        List<UUID> adminsId,
        Instant createdAt
) implements ChatResponse{
}
