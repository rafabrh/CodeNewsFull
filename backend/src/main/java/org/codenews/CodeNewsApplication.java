package org.codenews;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableConfigurationProperties
@EnableKafka
@EnableScheduling
@SpringBootApplication(scanBasePackages = "org.codenews")
public class CodeNewsApplication {
    public static void main(String[] args) {
        SpringApplication.run(CodeNewsApplication.class, args);
    }
}
