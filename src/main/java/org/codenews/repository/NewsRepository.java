package org.codenews.repository;


import org.codenews.model.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    List<News> findTop10ByOrderByPublishDateDesc();

    List<News> findAllByUrlIn(List<String> urls);

    boolean existsByUrl(String url);


}
