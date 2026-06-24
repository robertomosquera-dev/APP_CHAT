package com.rb.api_chat;

import com.rb.api_chat.util.MinioProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ApiChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiChatApplication.class, args);
    }

}
