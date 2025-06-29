package org.codenews.controller;

import lombok.RequiredArgsConstructor;
import org.codenews.dto.ArticlesTotalResponse;
import org.codenews.dto.NewsResponse;
import org.codenews.repository.NewsRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsRepository newsRepository;

    @GetMapping
    public ResponseEntity<List<NewsResponse>> findAll() {
        List<NewsResponse> all = newsRepository.findAll().stream().map(n -> new NewsResponse(
                n.getTitle(), n.getSummary(), n.getUrl(), n.getImageUrl(), n.getSource(), n.getPublishDate()
        )).collect(Collectors.toList());
        return ResponseEntity.ok(all);
    }

    @GetMapping("/latest")
    public ResponseEntity<List<NewsResponse>> findLatest10() {
        List<NewsResponse> latest = newsRepository.findTop10ByOrderByPublishDateDesc().stream().map(n -> new NewsResponse(
                n.getTitle(), n.getSummary(), n.getUrl(), n.getImageUrl(), n.getSource(), n.getPublishDate()
        )).collect(Collectors.toList());
        return ResponseEntity.ok(latest);
    }

    @GetMapping("/articles/total")
    public ResponseEntity<ArticlesTotalResponse> countArticles() {
        long total = newsRepository.count();  // JpaRepository.count()
        return ResponseEntity.ok(new ArticlesTotalResponse(total));
    }
}
