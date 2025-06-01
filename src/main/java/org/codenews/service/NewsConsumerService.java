package org.codenews.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codenews.config.NewsletterProperties;
import org.codenews.email.EmailService;
import org.codenews.model.News;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Serviço que consome as mensagens do Kafka (objetos News)
 * e acumula em buffer até atingir batchSize, então envia a newsletter.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NewsConsumerService {

    private final EmailService emailService;
    private final NewsletterProperties newsletterProperties;

    // Buffer em memória para acumular antes de disparar e-mail
    private final List<News> buffer = new ArrayList<>();

    @KafkaListener(
            topics = "${spring.kafka.topic.news}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(News news) {
        int nextSize = buffer.size() + 1;
        log.info("[NEWS_CONSUMER] Notícia recebida: '{}' (Buffer {}/{})",
                news.getTitle(),
                nextSize,
                newsletterProperties.getSize()
        );
        buffer.add(news);

        if (buffer.size() >= newsletterProperties.getSize()) {
            log.info("[NEWS_CONSUMER] Lote completo ({} mensagens). Disparando envio de newsletter...", buffer.size());
            emailService.sendNewsletter(new ArrayList<>(buffer));
            buffer.clear();
            log.info("[NEWS_CONSUMER] Buffer limpo após envio.");
        }
    }
}
