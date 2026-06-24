package com.rb.api_chat.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ChatResponse (
        UUID id,
        UUID senderId,
        UUID receiverId,
        Instant createdAt
){
}