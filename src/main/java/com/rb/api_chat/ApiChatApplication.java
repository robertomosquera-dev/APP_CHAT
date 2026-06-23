package com.rb.api_chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
public class ApiChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiChatApplication.class, args);
    }

}
