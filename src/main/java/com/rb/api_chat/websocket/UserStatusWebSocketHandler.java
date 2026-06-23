package com.rb.api_chat.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserStatusWebSocketHandler implements WebSocketHandler {

    private final UserStatusSink userStatusSink;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Flux<WebSocketMessage> outputMessages = userStatusSink
                .asFlux()
                .map(user -> {
                    try {
                        return session.textMessage(objectMapper.writeValueAsString(user));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

        // session.receive() se suscribe para mantener la conexión viva del lado del cliente
        // Mono.zip espera a que terminen ambos (lo cual no pasará hasta que el cliente se desconecte)
        Mono<Void> output = session.send(outputMessages);
        Mono<Void> input = session.receive().then();

        return Mono.zip(output, input).then();
    }
}