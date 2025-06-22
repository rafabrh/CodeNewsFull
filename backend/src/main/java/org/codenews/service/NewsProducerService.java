package org.codenews.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codenews.model.News;
import org.codenews.repository.NewsRepository;
import org.codenews.scraper.NewsScraperService;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${spring.kafka.topic.news}")
    private String topicName;

    public void executeDailyProducerPipeline() {
        log.info("[NEWS_PRODUCER] Solicitando até 10 notícias recentes ao Scraper...");
        List<News> scraped = newsScraperService.fetchTop10Latest();

        List<News> novas = new ArrayList<>();
        for (News n : scraped) {
            if (!newsRepository.existsByUrl(n.getUrl())) {
                newsRepository.save(n);
                novas.add(n);
            }
        }

        log.info("[NEWS_PRODUCER] Publicando {} notícias no Kafka.", scraped.size());
        publishBatch(scraped);

        News flushMarker = new News();
        flushMarker.setUrl("FLUSH");
        flushMarker.setTitle("");
        flushMarker.setPublishDate(LocalDateTime.now());
        kafkaTemplate.send(topicName, "FLUSH", flushMarker);
    }

    private void publishBatch(List<News> batch) {
        for (News news : batch) {
            kafkaTemplate.send(topicName, news.getUrl(), news)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("[NEWS_PRODUCER] Falha ao publicar notícia {}: {}", news.getUrl(), ex.getMessage());
                        }
                    });
        }
    }
}