package com.rb.api_chat.repository;

import com.rb.api_chat.dto.response.UserResponse;
import com.rb.api_chat.model.UserEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UserRepository extends ReactiveMongoRepository<UserEntity, UUID> {
    Mono<UserEntity> findByUsername(String username);
}
