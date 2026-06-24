package com.rb.api_chat.config;

import com.rb.api_chat.util.MinioProperties;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MinioConfig {

    private final MinioProperties minioProperties;

    @Bean
    public MinioClient minioClient(){
        return MinioClient
                .builder()
                .credentials(
                        minioProperties.accessKey(),
                        minioProperties.secretKey()
                )
                .endpoint(minioProperties.endpoint())
                .build();
    };

}
