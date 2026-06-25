package com.rb.api_chat.dto.response;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record ContactResponse(
        UUID id,
        String alias,
        String username,
        String numberPhone,
        String photoUrl,
        boolean online,
        Instant lastSeen,
        boolean blocked
) {
}
