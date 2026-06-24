package com.rb.api_chat.util;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "minio")
public record MinioProperties(
    String endpoint,
    String accessKey,
    String secretKey,
    String bucketName
) {
}
