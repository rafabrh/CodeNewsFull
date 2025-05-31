package org.codenews.controller;

import lombok.RequiredArgsConstructor;
import org.codenews.model.Subscriber;
import org.codenews.repository.SubscriberRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscribe")
@RequiredArgsConstructor
public class SubscriberController {

    private final SubscriberRepository repository;

    @PostMapping
    public ResponseEntity<?> subscribe(@RequestBody Subscriber subscriber) {
        if (repository.existsByEmail(subscriber.getEmail())) {
            return ResponseEntity.badRequest().body("E-mail já cadastrado.");
        }
        repository.save(subscriber);
        return ResponseEntity.ok("Cadastro realizado com sucesso!");
    }
}

