package com.rb.api_chat.service.impl;

import com.rb.api_chat.service.IStorageService;
import com.rb.api_chat.util.MinioProperties;
import io.minio.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioServiceImpl implements IStorageService {

    private final MinioProperties minioProperties;
    private final MinioClient minioClient;

    @PostConstruct
    public void init() {
        try {
            ensureBucketExists(minioProperties.bucketName());
        } catch (Exception e) {
            log.error("Error al inicializar MinIO", e);
        }
    }

    @Override
    public Mono<String> uploadFile(FilePart file, String folder) {
        String objectName = buildObjectName(folder, file.filename());
        return Mono
                .fromCallable(() -> uploadToMinio(file, objectName))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<String> updateFile(FilePart newFile, String folder, String oldFile) {
        if(oldFile == null) return uploadFile(newFile, folder);
        return deleteFile(oldFile)
                .then(uploadFile(newFile, folder));
    }

    @Override
    public Mono<Void> deleteFile(String fileName){
        return Mono.fromCallable(() -> {
            minioClient
                    .removeObject(
                            RemoveObjectArgs
                                    .builder()
                                    .bucket(minioProperties.bucketName())
                                    .object(fileName)
                                    .build()
                    );
            return null;
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    @Override
    public Flux<String> getAllUrl(Flux<String> fileNames){
        return fileNames.concatMap(this::getUrl);
    }

    @Override
    public Mono<String> getUrl(String fileName){
        return Mono.just("%s/%s/%s".formatted(minioProperties.endpoint(), minioProperties.bucketName(), fileName));
    }

    private void ensureBucketExists(String bucket) throws Exception {
        boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucket).build()
        );

        if (!exists) {
            createPublicBucket(bucket);
            log.info("Bucket '{}' creado y configurado como público", bucket);
        }
    }

    private void createPublicBucket(String bucket) throws Exception {
        minioClient.makeBucket(
                MakeBucketArgs.builder().bucket(bucket).build()
        );
        minioClient.setBucketPolicy(
                SetBucketPolicyArgs.builder()
                        .bucket(bucket)
                        .config(buildPublicReadPolicy(bucket))
                        .build()
        );
    }

    private String uploadToMinio(FilePart file, String objectName) throws Exception {
        Path tempFile = Files.createTempFile("upload-", file.filename());
        try {
            file.transferTo(tempFile).block();
            putObject(tempFile, objectName);
            return objectName;
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    private void putObject(Path tempFile, String objectName) throws Exception {
        try (InputStream inputStream = Files.newInputStream(tempFile)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.bucketName())
                            .object(objectName)
                            .stream(inputStream, Files.size(tempFile), -1L)
                            .contentType(Files.probeContentType(tempFile))
                            .build()
            );
        }
    }

    private String buildObjectName(String folder, String filename) {
        return "%s/%s-%s".formatted(folder, UUID.randomUUID(), filename);
    }

    private String buildPublicReadPolicy(String bucket) {
        return """
            {
              "Version": "2012-10-17",
              "Statement": [{
                "Effect": "Allow",
                "Principal": { "AWS": ["*"] },
                "Action": ["s3:GetObject"],
                "Resource": ["arn:aws:s3:::%s/*"]
              }]
            }
            """.formatted(bucket);
    }
}