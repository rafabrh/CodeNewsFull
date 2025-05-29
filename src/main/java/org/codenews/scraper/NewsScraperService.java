package org.codenews.scraper;

import lombok.RequiredArgsConstructor;
import org.codenews.model.News;
import org.codenews.repository.NewsRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsScraperService {

    private final NewsRepository newsRepository;

    public List<News> scrape() {
        List<News> scrapedNews = new ArrayList<>();

        try {
            // Exemplo de scraping do site TechCrunch
            Document doc = Jsoup.connect("https://techcrunch.com/").get();

            for (Element article : doc.select("div.post-block")) {
                String title = article.select("h2.post-block__title a").text();
                String url = article.select("h2.post-block__title a").attr("href");
                String summary = article.select("div.post-block__content").text();
                String source = "TechCrunch";
                LocalDateTime now = LocalDateTime.now();

                if (!title.isBlank() && !url.isBlank()) {
                    News news = new News(title, summary, url, source, now);
                    scrapedNews.add(news);
                }

                if (scrapedNews.size() >= 10) break; // pega só as 10 primeiras
            }
            // Pode repetir para outros sites similares...

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Salva no banco e retorna as notícias
        return newsRepository.saveAll(scrapedNews);
    }
}
