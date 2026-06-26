package com.rb.api_chat.repository;

import com.rb.api_chat.dto.response.ChatResponse;
import com.rb.api_chat.model.ChatEntity;
import com.rb.api_chat.model.ChatType;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ChatRepository extends ReactiveMongoRepository<ChatEntity, UUID> {
    Mono<ChatEntity> findByIdAndType(UUID id, ChatType type);
}
