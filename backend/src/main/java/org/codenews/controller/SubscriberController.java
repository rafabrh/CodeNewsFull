package org.codenews.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codenews.dto.SubscribeRequest;
import org.codenews.model.Subscriber;
import org.codenews.repository.SubscriberRepository;
import org.codenews.service.MetricsService;
import org.codenews.service.WelcomeEmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscribe")
@RequiredArgsConstructor
@Slf4j
public class SubscriberController {

    private final SubscriberRepository repository;
    private final WelcomeEmailService welcomeEmailService;
    private final MetricsService metricsService;

    @PostMapping
    public ResponseEntity<String> subscribe(@Valid @RequestBody SubscribeRequest request) {
        if (repository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("E-mail já cadastrado.");
        }

        Subscriber subscriber = Subscriber.builder()
                .email(request.getEmail())
                .name(request.getName())
                .build();

        repository.save(subscriber);
        metricsService.recordNewSubscriber();
        welcomeEmailService.sendWelcomeEmail(subscriber.getEmail());

        log.info("[SUBSCRIBE] Novo inscrito registrado: {}", subscriber.getEmail());
        return ResponseEntity.ok("Cadastro realizado com sucesso! Verifique seu e-mail.");
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<String> unsubscribe(@PathVariable String email) {
        var subscriberOpt = repository.findByEmail(email);

        if (subscriberOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        repository.delete(subscriberOpt.get());
        metricsService.recordUnsubscribe();

        log.info("[UNSUBSCRIBE] Inscrição cancelada para: {}", email);
        return ResponseEntity.ok("E-mail " + email + " removido com sucesso.");
    }

    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAllSubscribers() {
        repository.deleteAll();
        log.warn("[ADMIN] Todos os subscribers foram deletados manualmente.");
        return ResponseEntity.noContent().build();
    }
}
