package org.codenews.service;


import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.codenews.email.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsConsumerService {

    private final EmailService emailService;
    private final StringBuilder buffer = new StringBuilder();
    private static final int BATCH_SIZE = 10;

    @KafkaListener(topics = "techpulse-news", groupId = "techpulse-group")
    public void consume(ConsumerRecord<String, String> record) {
        buffer.append(record.value()).append("\n\n");

        // Envia email quando acumular 10 notícias
        if (buffer.toString().split("\n\n").length >= BATCH_SIZE) {
            // TODO: adaptar para enviar para múltiplos clientes - agora envia fixo
            emailService.sendNewsletter("cliente@example.com", buffer.toString());
            buffer.setLength(0); // limpa buffer
        }
    }
}
