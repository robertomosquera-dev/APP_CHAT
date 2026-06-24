package com.rb.api_chat.controller;

import com.rb.api_chat.service.IEmailService;
import com.rb.api_chat.service.IGenerateCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/test-email")
@RequiredArgsConstructor
public class TestEmailController {

    private final IEmailService emailService;

    private final IGenerateCodeService generateCodeService;

    @PostMapping("/send/{email}")
    public Mono<Void> sendEmail(@PathVariable String email){
        return generateCodeService
                .generateCode()
                .map(Object::toString)
                .flatMap(code -> emailService.sendVerificationEmail(email, code));
    }

}
