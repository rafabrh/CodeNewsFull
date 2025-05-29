package org.codenews;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableScheduling
@EnableKafka
public class CodeNewsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeNewsApplication.class, args);
    }
}