package com.rb.api_chat.model;

import java.util.Set;

public enum MessageStatus {
    SENT {
        @Override
        public Set<MessageStatus> getValidNextStatuses() {
            return Set.of(DELIVERED, READ);
        }
    },
    DELIVERED {
        @Override
        public Set<MessageStatus> getValidNextStatuses() {
            return Set.of(READ);
        }
    },
    READ;

    public Set<MessageStatus> getValidNextStatuses() {
        return Set.of();
    }

    public MessageStatus transitionTo(MessageStatus next) {
        if (!getValidNextStatuses().contains(next)) {
            throw new IllegalStateException(
                    "No se puede cambiar de " + this + " a " + next
            );
        }
        return next;
    }
}