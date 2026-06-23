package com.rb.api_chat.websocket;

import com.rb.api_chat.dto.response.UserResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
public class UserStatusSink {

    private final Sinks.Many<UserResponse> sink = Sinks
            .many()
            .multicast()
            .onBackpressureBuffer();

    public void emit(UserResponse user) {
        sink.tryEmitNext(user);
    }

    public Flux<UserResponse> asFlux() {
        return sink.asFlux();
    }
}