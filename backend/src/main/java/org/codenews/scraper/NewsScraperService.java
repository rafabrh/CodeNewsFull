package org.codenews.scraper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codenews.model.News;
import org.codenews.repository.NewsRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsScraperService {

    private final NewsRepository newsRepository;

    @Value("${app.scraper.rss-url}")
    private String rssUrl;

    public List<News> fetchTop10Latest() {
        log.info("[SCRAPER] Iniciando coleta de notícias");
        List<News> coletadas = new ArrayList<>();

        try {
            URL url = new URL(rssUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            InputStream stream = conn.getInputStream();

            var builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            var docXml = builder.parse(stream);
            var items = docXml.getElementsByTagName("item");
            DateTimeFormatter rfc1123 = DateTimeFormatter.RFC_1123_DATE_TIME;

            for (int i = 0; i < items.getLength() && coletadas.size() < 10; i++) {
                var node = items.item(i);
                if (!(node instanceof org.w3c.dom.Element)) continue;
                var itemElem = (org.w3c.dom.Element) node;

                String title = getTagValue("title", itemElem);
                String link = getTagValue("link", itemElem);
                String pubDateStr = getTagValue("pubDate", itemElem);
                ZonedDateTime zdt = ZonedDateTime.parse(pubDateStr, rfc1123);
                LocalDateTime publishDate = zdt.withZoneSameInstant(ZoneId.of("America/Sao_Paulo")).toLocalDateTime();

                String imageUrl = extractImageFromPage(link);

                News news = new News();
                news.setTitle(title);
                news.setUrl(link);
                news.setPublishDate(publishDate);
                news.setImageUrl(imageUrl);

                coletadas.add(news);
                log.info("[SCRAPER] Notícia coletada: {}", title);
            }

        } catch (Exception e) {
            log.error("[SCRAPER] Erro ao acessar RSS: {}", e.getMessage());
        }

        if (coletadas.isEmpty()) {
            log.warn("[SCRAPER] Fallback para últimas 10 do BD.");
            return newsRepository.findTop10ByOrderByPublishDateDesc();
        }

        return coletadas;
    }

    private String getTagValue(String tag, org.w3c.dom.Element element) {
        var nl = element.getElementsByTagName(tag);
        if (nl.getLength() == 0) return "";
        return nl.item(0).getTextContent().trim();
    }

    /**
     * Faz scraping da página da notícia para capturar a imagem (Open Graph og:image).
     */
    private String extractImageFromPage(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(5000)
                    .get();

            String ogImage = doc.select("meta[property=og:image]").attr("content");

            if (ogImage != null && !ogImage.isBlank()) {
                log.info("[SCRAPER] Imagem encontrada para {}: {}", url, ogImage);
                return ogImage;
            } else {
                log.warn("[SCRAPER] Nenhuma imagem og:image encontrada para {}", url);
            }
        } catch (Exception e) {
            log.warn("[SCRAPER] Erro ao fazer scraping da página {}: {}", url, e.getMessage());
        }
        return "";
    }
}
