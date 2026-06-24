package com.rb.api_chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record ChangeAliasContactRequest(
        @NotNull(message = "El ID del contacto es obligatorio.")
        UUID contactId,

        @NotBlank(message = "El nuevo alias no puede estar vacío.")
        @Size(max = 50, message = "El alias no puede superar los 50 caracteres.")
        String alias
) {
}