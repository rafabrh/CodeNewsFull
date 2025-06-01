package org.codenews.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Faz o binding de:
 *   spring.kafka.newsletter.batch.size
 * dentro de application.yml.
 */

@Component
@ConfigurationProperties(prefix = "spring.kafka.newsletter.batch")
public class NewsletterProperties {

/**
     * Vai mapear o valor de
     *   spring.kafka.newsletter.batch.size: 10
     */

    private int size;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
