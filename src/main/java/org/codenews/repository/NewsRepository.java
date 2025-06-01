package org.codenews.repository;

import org.codenews.model.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {
    @Query(value = "SELECT * FROM news ORDER BY publish_date DESC LIMIT :n", nativeQuery = true)
    List<News> findTopNByOrderByPublishDateDesc(@Param("n") int n);
    boolean existsByUrl(String url);


    List<News> findTop10ByOrderByPublishDateDesc();
}
