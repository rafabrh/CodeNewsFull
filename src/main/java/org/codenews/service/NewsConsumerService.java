package org.codenews.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codenews.model.Subscriber;
import org.codenews.repository.SubscriberRepository;
import org.codenews.email.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NewsConsumerService {

    private final ObjectMapper objectMapper;
    private final SubscriberRepository subscriberRepository;
    private final EmailService emailService;

    @KafkaListener(topics = "codenews-news",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message) throws JsonProcessingException {
        JsonNode node = objectMapper.readTree(message);
        // lê o content inteiro
        String content = node.path("content").asText(null);
        if (content == null) {
            log.error("Não veio `content` no payload, pulando: {}", message);
            return;
        }

        int sep = content.indexOf(" - ");
        if (sep < 0) {
            log.error("Formato inesperado de content (esperado 'Título - URL'): {}", content);
            return;
        }
        String title = content.substring(0, sep).trim();
        String url   = content.substring(sep + 3).trim();

            List<Subscriber> subscribers = subscriberRepository.findAll();
            subscribers.forEach(sub -> {
                log.info("Enviando newsletter '{}' para {}", title, sub.getEmail());
                emailService.sendNewsletter(sub.getEmail(), content);
            });

    }
}
