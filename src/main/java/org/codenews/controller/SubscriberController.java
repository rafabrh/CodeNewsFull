// src/main/java/org/codenews/controller/SubscriberController.java
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

    /**
     * POST /api/subscribe
     * Cadastra um novo assinante (se o e-mail ainda não estiver cadastrado).
     */
    @PostMapping
    public ResponseEntity<String> subscribe(@RequestBody Subscriber subscriber) {
        if (repository.existsByEmail(subscriber.getEmail())) {
            return ResponseEntity.badRequest().body("E-mail já cadastrado.");
        }
        repository.save(subscriber);
        return ResponseEntity.ok("Cadastro realizado com sucesso!");
    }

    /**
     * DELETE /api/subscribe/{email}
     * Remove um assinante pelo e-mail, caso queira parar de receber a newsletter.
     */
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

    /**
     * DELETE /api/subscribe/all
     * Cancela a inscrição de todos os assinantes (útil para testes ou reset).
     */
    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAllSubscribers() {
        repository.deleteAll();
        return ResponseEntity.noContent().build();
    }
}
