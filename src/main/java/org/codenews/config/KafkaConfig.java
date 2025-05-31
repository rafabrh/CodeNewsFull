package org.codenews.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic newsTopic() {
        return new NewTopic("codenews-news", 1, (short) 1);
    }
}
