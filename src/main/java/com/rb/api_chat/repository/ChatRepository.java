package com.rb.api_chat.repository;

import com.rb.api_chat.model.ChatEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChatRepository extends ReactiveMongoRepository<ChatEntity, UUID> {
}
