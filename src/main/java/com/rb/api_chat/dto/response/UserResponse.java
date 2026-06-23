package com.rb.api_chat.dto.response;
import java.util.List;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        List<String> rolesName
) {
}
