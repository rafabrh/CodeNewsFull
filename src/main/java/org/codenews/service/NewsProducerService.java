package org.codenews.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codenews.config.NewsletterProperties;
import org.codenews.model.News;
import org.codenews.repository.NewsRepository;
import org.codenews.scraper.NewsScraperService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço responsável por:
 *  1) Carregar as últimas notícias do banco.
 *  2) Se houver menos que batchSize, disparar o scraper.
 *  3) Salvar no banco e publicar no Kafka.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NewsProducerService {

    private final KafkaTemplate<String, News> kafkaTemplate;
    private final NewsRepository newsRepository;
    private final NewsScraperService newsScraperService;
    private final NewsletterProperties newsletterProperties;

    /**
     * Injetamos o nome do tópico via application.yml:
     *   spring.kafka.topic.news: codenews-news
     */
    @Value("${spring.kafka.topic.news}")
    private String topic;

    @Transactional
    public void runProducerPipeline() {
        int batchSize = newsletterProperties.getSize();

        // Busca as últimas N notícias já existentes
        List<News> latestNews = newsRepository.findTop10ByOrderByPublishDateDesc();
        log.info("[NEWS_PRODUCER] Últimas notícias no banco: {}", latestNews.size());

        // Se já houver o suficiente, não faz scraping
        if (latestNews.size() >= batchSize) {
            log.info("[NEWS_PRODUCER] Já existem {} notícias, nenhum scraping necessário.", latestNews.size());
            return;
        }

        log.info("[NEWS_PRODUCER] Menos de {} notícias no banco ({}). Iniciando scraping...",
                batchSize, latestNews.size());

        // Chama o scraper (já retorna até 'batchSize' itens)
        List<News> scraped = newsScraperService.scrapeLatestNews();

        // Filtra duplicatas por URL
        List<News> newOnes = scraped.stream()
                .filter(n -> !newsRepository.existsByUrl(n.getUrl()))
                .collect(Collectors.toList());

        if (newOnes.isEmpty()) {
            log.warn("[NEWS_PRODUCER] Scraper não retornou notícias novas (todas já existiam).");
            return;
        }

        // Salvar cada notícia nova no banco e publicar no Kafka
        newOnes.forEach(news -> {
            newsRepository.save(news);
            kafkaTemplate.send(topic, news.getUrl(), news);
            log.info("[NEWS_PRODUCER] Nova notícia salva e publicada: {} ({})",
                    news.getTitle(), news.getUrl());
        });

        log.info("[NEWS_PRODUCER] Pipeline concluído. {} notícias novas publicadas.", newOnes.size());
    }

    /**
     * Método auxiliar para publicar um lote arbitrário, se necessário.
     */
    public void publishBatch(List<News> newsList) {
        int success = 0;
        for (News news : newsList) {
            kafkaTemplate.send(topic, news.getUrl(), news);
            log.info("[NEWS_PRODUCER] Publicada notícia em batch: {} ({})", news.getTitle(), news.getUrl());
            success++;
        }
        log.info("[NEWS_PRODUCER] Publicação em batch concluída. {}/{} enviados.", success, newsList.size());
    }
}
