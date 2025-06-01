package org.codenews.scraper;

import lombok.extern.slf4j.Slf4j;
import org.codenews.model.News;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class NewsScraperService {

    /**
     * Raspa até `limit` notícias mais recentes do portal Canaltech.
     */
    public List<News> scrapeLatestNews(int limit) {
        List<News> newsList = new ArrayList<>();
        try {
            Document doc = Jsoup.connect("https://canaltech.com.br/ultimas/").get();
            for (Element card : doc.select(".card.card-article")) {
                String title = card.select(".card-title").text();
                String summary = card.select(".card-description").text();
                String url = card.select("a").attr("href");
                if (!url.startsWith("http")) {
                    url = "https://canaltech.com.br" + url;
                }
                String imageUrl = card.select("img").attr("src");
                String source = "Canaltech";
                LocalDateTime publishDate = LocalDateTime.now(); // data fictícia

                newsList.add(new News(title, summary, url, imageUrl, source, publishDate));
                if (newsList.size() >= limit) {
                    break;
                }
            }
        } catch (Exception e) {
            log.error("[NewsScraperService] Erro ao fazer scraping: {}", e.getMessage(), e);
        }
        return newsList;
    }

    /**
     * Versão sem parâmetros (compatibilidade) — usa 10 notícias por padrão.
     */
    public List<News> scrapeLatestNews() {
        return scrapeLatestNews(10);
    }
}
