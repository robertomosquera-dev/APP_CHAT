package com.rb.api_chat.dto.request;

import lombok.Builder;
import org.hibernate.validator.constraints.Length;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.List;
import java.util.UUID;

@Builder
public record UserRegisterRequest(
        @NotBlank(message = "El username es obligatorio")
        @Length(min = 6, max = 20,
                message = "El username debe tener entre 6 y 20 caracteres")
        @Pattern(
                regexp = "^[a-zA-Z0-9._]+$",
                message = "El username solo puede contener letras, números, puntos y guiones bajos"
        )
        String username,
        @NotBlank(message = "El nombre es obligatorio")
        @Length(min = 2, max = 50,
                message = "El nombre debe tener entre 2 y 50 caracteres")
        String firstName,
        @NotBlank(message = "El apellido es obligatorio")
        @Length(min = 2, max = 50,
                message = "El apellido debe tener entre 2 y 50 caracteres")
        String lastName,
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato válido")
        @Length(max = 100,
                message = "El correo no puede superar los 100 caracteres")
        String email,
        @NotBlank(message = "El número telefónico es obligatorio")
        @Pattern(
                regexp = "^\\+?[0-9]{8,15}$",
                message = "El número telefónico no tiene un formato válido"
        )
        String phoneNumber,
        List<UUID> rolesId
) {

}
