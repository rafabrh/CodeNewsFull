package org.codenews.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codenews.config.NewsletterProperties;
import org.codenews.model.News;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsConsumerService {

    private final EmailService emailService;
    private final NewsletterProperties newsletterProperties;

    private final List<News> buffer = new ArrayList<>();

    @KafkaListener(
            topics = "${spring.kafka.topic.news}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(News news) {
        // Ignora o “flush marker” propriamente dito
        if ("FLUSH".equalsIgnoreCase(news.getUrl())) {
            log.info("[NEWS_CONSUMER] Marker FLUSH recebido. Se houver itens no buffer, disparar envio imediato.");
            synchronized (buffer) {
                if (!buffer.isEmpty()) {
                    log.info("[NEWS_CONSUMER] Buffer possui {} notícia(s). Enviando newsletter no marker FLUSH...", buffer.size());
                    try {
                        emailService.sendNewsletter(new ArrayList<>(buffer));
                        log.info("[NEWS_CONSUMER] E-mail enviado via marker FLUSH. Limpando buffer.");
                    } catch (Exception e) {
                        log.error("[NEWS_CONSUMER] Falha ao enviar e-mail via marker FLUSH: {}", e.getMessage(), e);
                    }
                    buffer.clear();
                } else {
                    log.info("[NEWS_CONSUMER] Buffer vazio ao receber marker FLUSH. Nada a enviar.");
                }
            }
            return;
        }

        // Caso seja notícia “de verdade”, adiciona ao buffer
        synchronized (buffer) {
            buffer.add(news);
            log.info("[NEWS_CONSUMER] Notícia recebida: '{}' (Buffer size {}/{})",
                    news.getTitle(),
                    buffer.size(),
                    newsletterProperties.getSize());

            if (buffer.size() >= newsletterProperties.getSize()) {
                log.info("[NEWS_CONSUMER] Lote completo ({} mensagens). Disparando envio de newsletter...", buffer.size());
                try {
                    emailService.sendNewsletter(new ArrayList<>(buffer));
                    log.info("[NEWS_CONSUMER] E-mail enviado via Consumer. Limpando buffer.");
                } catch (Exception e) {
                    log.error("[NEWS_CONSUMER] Falha ao enviar e-mail via Consumer: {}", e.getMessage(), e);
                }
                buffer.clear();
            }
        }
    }

    /**
     * Se, até as 8:05, houver notícias restantes no buffer (lote incompleto),
     * dispara o envio com o que tiver e limpa o buffer.
     */
    @Scheduled(cron = "0 05 8 * * *", zone = "America/Sao_Paulo")
    public void flushIfIncomplete() {
        synchronized (buffer) {
            if (buffer.isEmpty()) {
                log.info("[NEWS_CONSUMER] Buffer vazio no flush agendado. Nada a enviar.");
                return;
            }
            log.info("[NEWS_CONSUMER] Flush agendado: Buffer possui {} notícia(s). Enviando e-mail com itens pendentes...", buffer.size());
            try {
                emailService.sendNewsletter(new ArrayList<>(buffer));
                log.info("[NEWS_CONSUMER] E-mail enviado no flush agendado. Limpando buffer.");
            } catch (Exception e) {
                log.error("[NEWS_CONSUMER] Falha ao enviar e-mail no flush agendado: {}", e.getMessage(), e);
            }
            buffer.clear();
        }
    }
}
