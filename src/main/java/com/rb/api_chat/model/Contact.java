package com.rb.api_chat.model;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contact {
    private UUID userId;
    @Builder.Default
    private Boolean blocked = false;
    private String alias;
}
