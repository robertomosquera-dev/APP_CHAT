package com.rb.api_chat.model;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatus {
    private boolean online;
    private Instant lastSeen;
}
