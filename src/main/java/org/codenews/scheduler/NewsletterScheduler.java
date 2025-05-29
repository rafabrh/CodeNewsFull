package org.codenews.scheduler;

import org.codenews.model.News;
import lombok.RequiredArgsConstructor;
import org.codenews.scraper.NewsScraperService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NewsletterScheduler {

    private final NewsScraperService newsScraperService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    // Executa todo dia às 8h da manhã
    @Scheduled(cron = "0 0 8 * * ?")
    public void generateNewsletter() {
        List<News> newsList = newsScraperService.scrape();

        // Envia título e url para o tópico Kafka
        newsList.forEach(news -> {
            String message = news.getTitle() + " - " + news.getUrl();
            kafkaTemplate.send("codenews-news", message);
        });
    }
}
