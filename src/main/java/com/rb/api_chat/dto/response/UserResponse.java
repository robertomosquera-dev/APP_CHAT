package com.rb.api_chat.dto.response;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder(toBuilder = true)
public record UserResponse(
        UUID id,
        String username,
        String firstName,
        String lastName,
        String email,
        String photoUrl,
        Boolean status,
        Instant lastSeen,
        String phoneNumber,
        List<String> rolesName
) {
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
