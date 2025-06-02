package org.codenews.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codenews.config.NewsletterProperties;
import org.codenews.service.EmailService;
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
            try {
                emailService.sendNewsletter(new ArrayList<>(buffer));
                log.info("[NEWS_CONSUMER] E-mail enviado via Consumer. Limpando buffer.");
            } catch (Exception e) {
                log.error("[NEWS_CONSUMER] Falha ao enviar e-mail via Consumer: {}", e.getMessage(), e);
            }
            buffer.clear();
        }
    }

    /**
     * Agendador que roda X minutos após o horário de scraping (por exemplo, 08:05),
     * garantindo que, mesmo se buffer.size() < batchSize, as notícias pendentes sejam enviadas.
     * Ajuste o cron para seu fuso/horário desejado.
     */
    @Scheduled(cron = "0 05 8 * * *", zone = "America/Sao_Paulo")
    public void flushIfIncomplete() {
        synchronized (buffer) {
            if (buffer.isEmpty()) {
                log.info("[NEWS_CONSUMER] Buffer vazio no flush agendado. Nada a enviar.");
                return;
            }
            log.info("[NEWS_CONSUMER] Flush agendado: Buffer possui {} notícia(s). Enviando e-mail com complementos do BD...", buffer.size());

            // Caso faltem N itens para completar batchSize, buscamos no BD para compor.
            int faltam = newsletterProperties.getSize() - buffer.size();
            if (faltam > 0) {
                // Busca noticias do BD para completar (novas mais recentes, evitando duplicar URLs do buffer)
                // (Aqui podemos implementar lógica de complemento, mas como o producer já enviou TODAS
                //  as 10 últimas ao Kafka, em geral o buffer já terá até 10. Este flush serve mais para
                //  dias em que o producer enviou < 10 itens ao Kafka: então buffer.size()<10 e faltam open slots.)
                // Para simplicidade, neste passo podemos ignorar e apenas enviar o buffer (mesmo com <10).
            }

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
