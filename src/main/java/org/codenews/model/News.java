package org.codenews.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Protege o construtor padrão sem quebrar o JPA
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String summary;

    @Column(nullable = false, unique = true)
    private String url;

    private String source;

    private LocalDateTime publishDate;

    /**
     * Construtor principal para criação de instâncias de News.
     */
    public News(String title, String summary, String url, String source, LocalDateTime publishDate) {
        this.title = title;
        this.summary = summary;
        this.url = url;
        this.source = source;
        this.publishDate = publishDate;
    }
}
