package com.rb.api_chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContactToUserRequest(
        @NotBlank(message = "El número de teléfono es obligatorio.")
        @Size(min = 8, max = 15, message = "El número debe tener entre 8 y 15 caracteres.")
        String number,

        @NotBlank(message = "El alias es obligatorio.")
        @Size(max = 50, message = "El alias no puede superar los 50 caracteres.")
        String alias
) {
}