package org.codenews.controller;

import lombok.RequiredArgsConstructor;
import org.codenews.model.News;
import org.codenews.repository.NewsRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsRepository newsRepository;

    @GetMapping
    public ResponseEntity<List<News>> findAll() {
        List<News> all = newsRepository.findAll();
        return ResponseEntity.ok(all);
    }

    @GetMapping("/latest")
    public ResponseEntity<List<News>> findLatest10() {
        List<News> latest = newsRepository.findTop10ByOrderByPublishDateDesc();
        return ResponseEntity.ok(latest);
    }
}
