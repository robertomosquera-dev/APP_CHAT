package com.rb.api_chat.dto.request;

import com.rb.api_chat.model.MessageType;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MessageRequest(
        @NotNull(message = "El senderId es requerido")
        UUID senderId,
        UUID chatId,
        UUID receiverId,
        @Size(max = 1000, message = "El contenido no puede superar los 1000 caracteres")
        String content,
        @NotNull(message = "El tipo de mensaje es requerido")
        MessageType type
) {}