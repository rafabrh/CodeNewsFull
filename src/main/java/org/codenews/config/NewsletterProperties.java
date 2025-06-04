// src/main/java/org/codenews/config/NewsletterProperties.java
package org.codenews.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.kafka.newsletter.batch")
public class NewsletterProperties {

    /**
     * Tamanho do lote de notícias antes de disparar a newsletter.
     */
    private int size;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
