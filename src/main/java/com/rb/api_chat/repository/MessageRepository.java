package com.rb.api_chat.repository;

import com.rb.api_chat.model.MessageEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface MessageRepository extends ReactiveMongoRepository<MessageEntity, UUID> {
    Flux<MessageEntity> findByChatId(UUID chatId, Pageable pageable);
}
