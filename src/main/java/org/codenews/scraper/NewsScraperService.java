package org.codenews.scraper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codenews.model.News;
import org.codenews.repository.NewsRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço de scraping/raspagem de notícias CanalTech usando o RSS oficial:
 *   https://feeds2.feedburner.com/canaltechbr
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NewsScraperService {

    private final NewsRepository newsRepository;

    public List<News> fetchTop10Latest() {
        log.info("[SCRAPER] Iniciando coleta de até 10 notícias via RSS CanalTech");

        List<News> coletadas = new ArrayList<>();
        String rssUrl = "https://feeds2.feedburner.com/canaltechbr";

        try {
            // 1) Abre conexão HTTP com o RSS
            URL url = new URL(rssUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setConnectTimeout(10_000);
            conn.setReadTimeout(10_000);
            InputStream stream = conn.getInputStream();

            // 2) Parseia o XML via DocumentBuilder
            var factory = DocumentBuilderFactory.newInstance();
            var builder = factory.newDocumentBuilder();
            var docXml = builder.parse(stream);
            org.w3c.dom.NodeList items = docXml.getElementsByTagName("item");

            // 3) Monta um DateTimeFormatter para RFC-1123 (ex.: Tue, 02 Jun 2025 04:00:00 +0000)
            DateTimeFormatter rfc1123 = DateTimeFormatter.RFC_1123_DATE_TIME;

            // 4) Para cada <item> até coletar 10:
            for (int i = 0; i < items.getLength() && coletadas.size() < 10; i++) {
                var node = items.item(i);
                if (!(node instanceof org.w3c.dom.Element)) {
                    continue;
                }
                org.w3c.dom.Element itemElem = (org.w3c.dom.Element) node;

                String title      = getTagValue("title", itemElem).trim();
                String link       = getTagValue("link", itemElem).trim();
                String pubDateStr = getTagValue("pubDate", itemElem).trim();

                if (title.isEmpty() || link.isEmpty() || pubDateStr.isEmpty()) {
                    continue;
                }

                // Parse <pubDate> (UTC), depois converte para America/Sao_Paulo
                ZonedDateTime zdt;
                try {
                    zdt = ZonedDateTime.parse(pubDateStr, rfc1123);
                } catch (Exception e) {
                    // Se não conseguir parsear, assume agora UTC
                    zdt = ZonedDateTime.now(ZoneId.of("UTC"));
                }
                LocalDateTime publishDateTime = zdt
                        .withZoneSameInstant(ZoneId.of("America/Sao_Paulo"))
                        .toLocalDateTime();

                // Extrai <description> (HTML) → summary + imagem dentro dela
                String descHtml = getTagValue("description", itemElem);
                String summary  = "";
                String imageUrl = null;
                if (descHtml != null && !descHtml.isEmpty()) {
                    Document jsoupDoc = Jsoup.parse(descHtml);
                    summary = jsoupDoc.text();
                    var imgEl = jsoupDoc.selectFirst("img");
                    if (imgEl != null) {
                        imageUrl = imgEl.attr("src");
                    }
                }

                // Agora monta o objeto News
                News news = new News();
                news.setTitle(title);
                news.setSummary(summary);
                news.setUrl(link);
                news.setImageUrl(imageUrl);
                news.setSource("CanalTech-RSS");
                news.setPublishDate(publishDateTime);

                coletadas.add(news);
                log.debug("[SCRAPER] RSS coletou notícia: \"{}\" (URL={})", title, link);
            }

            log.info("[SCRAPER] RSS CanalTech retornou {} item(ns)", coletadas.size());

        } catch (Exception e) {
            log.error("[SCRAPER] Erro ao acessar RSS CanalTech: {}. Pulando coleta.", e.getMessage());
        }

        // Se vier ao menos 1 item (até 10), devolve exatamente essa lista (pode vir < 10)
        if (!coletadas.isEmpty()) {
            return coletadas;
        }

        // Se não trouxer nada do RSS (ou der erro), fazemos fallback para o BD
        log.warn("[SCRAPER] RSS vazio ou falhou. Fazendo fallback para últimas 10 do BD.");
        return fetchLast10FromDatabase();
    }

    /**
     * Retorna as últimas 10 notícias do BD, ordenadas por publishDate desc.
     */
    private List<News> fetchLast10FromDatabase() {
        try {
            List<News> ultimas10 = newsRepository.findTop10ByOrderByPublishDateDesc();
            log.info("[SCRAPER-FALLBACK] Retornando últimas {} notícias do BD.", ultimas10.size());
            return ultimas10;
        } catch (Exception e) {
            log.error("[SCRAPER-FALLBACK] Erro ao buscar últimas notícias no BD: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Pega o conteúdo textual do primeiro elemento <tag> dentro de itemElem,
     * ou string vazia se não existir.
     */
    private String getTagValue(String tag, org.w3c.dom.Element element) {
        var nl = element.getElementsByTagName(tag);
        if (nl.getLength() == 0) {
            return "";
        }
        return nl.item(0).getTextContent();
    }
}
