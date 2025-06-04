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
    public ResponseEntity<String> subscribe(@RequestBody Subscriber subscriber) {
        if (repository.existsByEmail(subscriber.getEmail())) {
            return ResponseEntity.badRequest().body("E-mail já cadastrado.");
        }
        repository.save(subscriber);
        return ResponseEntity.ok("Cadastro realizado com sucesso!");
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<String> unsubscribe(@PathVariable String email) {
        return repository.findAll().stream()
                .filter(s -> s.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .map(s -> {
                    repository.delete(s);
                    return ResponseEntity.ok("E-mail " + email + " removido com sucesso.");
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAllSubscribers() {
        repository.deleteAll();
        return ResponseEntity.noContent().build();
    }
}
