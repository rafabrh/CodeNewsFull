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
            Document doc = Jsoup.connect("https://canaltech.com.br/").get();

            for (Element article : doc.select("article")) {
                String title = article.select("h2").text();
                String url = article.select("a").attr("href");
                if (!url.startsWith("http")) {
                    url = "https://canaltech.com.br" + url;
                }
                String summary = article.select("p").text();
                String source = "Canaltech";
                LocalDateTime now = LocalDateTime.now();

                // Verifica duplicidade no banco
                if (!title.isBlank() && !url.isBlank() && !newsRepository.existsByUrl(url)) {
                    News news = new News(title, summary, url, source, now);
                    scrapedNews.add(news);
                }

                // Mesmo com limite de 10, não irá salvar duplicadas
                if (scrapedNews.size() >= 10) break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Salva no banco somente se houver novas notícias
        return scrapedNews.isEmpty() ? List.of() : newsRepository.saveAll(scrapedNews);
    }
}
