package com.rb.api_chat.service.impl;

import com.rb.api_chat.service.IEmailService;
import com.rb.api_chat.service.IGenerateCodeService;
import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements IEmailService {

    private final Resend resend;

    @Value("${resend.from}")
    private String from;

    @Override
    public Mono<Void> sendVerificationEmail(String to, String code) {
        return Mono
                .fromCallable(() -> {
                    CreateEmailOptions createEmailOptions = CreateEmailOptions
                            .builder()
                            .from(from)
                            .to(to)
                            .subject("Código de verificación")
                            .html(loadHtmlVerificationTemplate(code))
                            .build();
                    return resend.emails().send(createEmailOptions);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(r -> log.info("Email enviado a {}, id: {}", to, r.getId()))
                .doOnError(e -> log.error("Error al enviar email a {}", to, e))
                .then();
    }


    private String loadHtmlVerificationTemplate(String code) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/code.html");
            String html = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

            String digits = code.chars()
                    .mapToObj(c -> "<td style=\"background:#1c1c1c;border:1px solid #2e2e2e;border-radius:12px;" +
                            "width:52px;height:68px;text-align:center;vertical-align:middle;" +
                            "font-size:28px;font-weight:500;color:#f0f0f0;padding:0 10px;\">" +
                            (char) c + "</td>")
                    .collect(Collectors.joining("\n"));

            return html.replace("{{code}}", digits);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar el template de email", e);
        }
    }

}
