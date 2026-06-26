package com.rb.api_chat.dto.response;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;


public interface ChatResponse {
    UUID chatId();
}