package org.codenews.repository;

import org.codenews.model.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    /**
     * Verifica se já existe notícia com a URL informada.
     */
    boolean existsByUrl(String url);

    /**
     * Retorna as 10 últimas notícias ordenadas por publishDate (desc).
     * Usado pelo fallback do scraper.
     */
    List<News> findTop10ByOrderByPublishDateDesc();
}
