package com.rb.api_chat.service;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IStorageService {
    Mono<String> uploadFile(FilePart file, String folder);
    Flux<String> getAllUrl(Flux<String> fileNames);
    Mono<String> getUrl(String fileName);
    Mono<Void> deleteFile(String fileName);
    Mono<String> uploadFile(FilePart newFile, String folder, String oldFile);
}
