package org.codenews.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "news")
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(unique = true, nullable = false)
    private String url;

    private String imageUrl;
    private String source;
    private LocalDateTime publishDate;

    public News(String title, String summary, String url, String imageUrl, String source, LocalDateTime publishDate) {
        this.title = title;
        this.summary = summary;
        this.url = url;
        this.imageUrl = imageUrl;
        this.source = source;
        this.publishDate = publishDate;
    }
}
