package org.codenews.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codenews.model.News;
import org.codenews.repository.NewsRepository;
import org.codenews.scraper.NewsScraperService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsProducerService {

    private final NewsScraperService newsScraperService;
    private final NewsRepository newsRepository;
    private final KafkaTemplate<String, News> kafkaTemplate;

    private static final String TOPIC = "codenews-news";

    /**
     * 1) Coleta até 10 notícias via scraper (RSS)
     * 2) Persiste apenas URLs novas
     * 3) Publica todo o lote no Kafka (novas + duplicatas)
     * 4) Envia um “marker de flush” ao final para sinalizar que o lote terminou
     */
    public void executeDailyProducerPipeline() {
        log.info("[NEWS_PRODUCER] Solicitando até 10 notícias recentes ao Scraper...");
        List<News> scraped = newsScraperService.fetchTop10Latest();

        // 1) Persistência condicional: somente URLs novas
        List<News> somenteNovas = new ArrayList<>();
        for (News n : scraped) {
            if (!newsRepository.existsByUrl(n.getUrl())) {
                newsRepository.save(n);
                somenteNovas.add(n);
                log.debug("[NEWS_PRODUCER] Nova notícia persistida: {}", n.getUrl());
            } else {
                log.debug("[NEWS_PRODUCER] Notícia já existe (url={}), pulando persistência.", n.getUrl());
            }
        }

        // 2) Publica TODO o lote no Kafka (novas + duplicatas)
        log.info("[NEWS_PRODUCER] Publicando batch de {} notícias no Kafka (incluindo duplicatas).", scraped.size());
        publishBatch(scraped);

        // 3) Publica o “marker de flush” para que o consumidor saiba que terminou o lote
        News flushMarker = new News();
        flushMarker.setUrl("FLUSH"); // maracador único
        flushMarker.setTitle("");
        flushMarker.setSummary("");
        flushMarker.setImageUrl(null);
        flushMarker.setSource("SYSTEM");
        flushMarker.setPublishDate(LocalDateTime.now());
        kafkaTemplate.send(TOPIC, "FLUSH", flushMarker)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("[NEWS_PRODUCER] Falha ao publicar marker FLUSH: {}", ex.getMessage());
                    } else {
                        log.debug("[NEWS_PRODUCER] Marker FLUSH enviado com sucesso.");
                    }
                });
    }

    private void publishBatch(List<News> batch) {
        for (News news : batch) {
            log.info("[NEWS_PRODUCER] Publicando notícia no Kafka: {}", news.getUrl());
            kafkaTemplate.send(TOPIC, news.getUrl(), news)
                    .whenComplete((sendResult, ex) -> {
                        if (ex != null) {
                            log.error("[NEWS_PRODUCER] Falha ao publicar notícia {}: {}",
                                    news.getUrl(), ex.getMessage());
                        } else {
                            var md = sendResult.getRecordMetadata();
                            log.info("[NEWS_PRODUCER] Publicação bem-sucedida: topic={} partition={} offset={}",
                                    md.topic(), md.partition(), md.offset());
                        }
                    });
        }
    }
}
