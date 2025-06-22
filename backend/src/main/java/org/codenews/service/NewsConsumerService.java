package org.codenews.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codenews.config.NewsletterProperties;
import org.codenews.email.EmailService;
import org.codenews.model.News;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsConsumerService {

    private final EmailService emailService;
    private final NewsletterProperties newsletterProperties;

    private final Queue<News> buffer = new ConcurrentLinkedQueue<>();

    @KafkaListener(topics = "${spring.kafka.topic.news}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(News news) {
        if ("FLUSH".equalsIgnoreCase(news.getUrl())) {
            flushBuffer("Marker FLUSH");
            return;
        }

        buffer.add(news);
        log.info("[NEWS_CONSUMER] Buffer size: {}/{}", buffer.size(), newsletterProperties.getSize());

        if (buffer.size() >= newsletterProperties.getSize()) {
            flushBuffer("Batch completo");
        }
    }

    @Scheduled(cron = "0 5 8 * * *", zone = "America/Sao_Paulo")
    public void scheduledFlush() {
        flushBuffer("Flush agendado");
    }

    private void flushBuffer(String reason) {
        if (buffer.isEmpty()) {
            log.info("[NEWS_CONSUMER] Buffer vazio. Razão: {}", reason);
            return;
        }
        List<News> newsList = buffer.stream().collect(Collectors.toList());
        buffer.clear();
        try {
            emailService.sendNewsletter(newsList);
            log.info("[NEWS_CONSUMER] Newsletter enviada. Motivo: {}", reason);
        } catch (Exception e) {
            log.error("[NEWS_CONSUMER] Falha ao enviar newsletter: {}", e.getMessage());
        }
    }
}