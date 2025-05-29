package org.codenews.controller;

import lombok.RequiredArgsConstructor;
import org.codenews.model.News;
import org.codenews.repository.NewsRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsRepository newsRepository;

    @GetMapping
    public List<News> getLatestNews() {
        return newsRepository.findTop10ByOrderByPublishDateDesc();
    }
}
