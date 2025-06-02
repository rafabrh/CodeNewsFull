package org.codenews.scheduler;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codenews.service.PipelineService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsletterScheduler {

    private final PipelineService pipelineService;

    /**
     * Ao subir a aplicação, dispara o pipeline uma vez imediatamente
     * (que vai apenas publicar no Kafka).
     */
    @PostConstruct
    public void onStartup() {
        log.info("[SCHEDULER] Aplicação iniciada. Disparando pipeline imediatamente...");
        pipelineService.runPipeline();
        log.info("[SCHEDULER] Pipeline inicial executado.");
    }

    /**
     * Agendamento diário: executa todo dia às 08:00:00.
     * (Somente publica no Kafka; quem envia e-mail é o consumidor.)
     */
    @Scheduled(cron = "0 0 8 * * *", zone = "America/Sao_Paulo")
    public void scheduleDaily() {
        log.info("[SCHEDULER] Disparando pipeline agendado...");
        pipelineService.runPipeline();
        log.info("[SCHEDULER] Pipeline agendado finalizado.");
    }
}
