package org.codenews.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class NewsResponse {
    private String title;
    private String summary;
    private String url;
    private String imageUrl;
    private String source;
    private LocalDateTime publishDate;
}