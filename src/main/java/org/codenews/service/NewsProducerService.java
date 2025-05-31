package org.codenews.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.codenews.model.News;
import org.codenews.repository.NewsRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NewsProducerService {

    private static final String TOPIC = "codenews-news";

    private final NewsRepository newsRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void publishAllNewsOnStartup() {
        long total = newsRepository.count();
        log.info("Iniciando publicação de {} notícias no Kafka", total);
        newsRepository.findAll().forEach(this::sendToKafka);
    }

    private void sendToKafka(News news) {
        String payload = String.format(
                "{\"email\":\"%s\",\"content\":\"%s - %s\"}",
                "usuario@dominio.com", // no mundo real itere sobre seus subscribers
                news.getTitle(),
                news.getUrl()
        );

        kafkaTemplate.send(TOPIC, payload)
                // quando usar CompletableFuture:
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Erro ao enviar notícia id={} ao Kafka", news.getId(), ex);
                    } else {
                        RecordMetadata md = result.getRecordMetadata();
                        log.info(
                                "Notícia id={} publicada no Kafka (topic={}, partition={}, offset={})",
                                news.getId(), md.topic(), md.partition(), md.offset()
                        );
                    }
                });
    }
}
