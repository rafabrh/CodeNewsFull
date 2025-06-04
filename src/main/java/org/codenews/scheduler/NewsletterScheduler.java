// src/main/java/org/codenews/scheduler/NewsletterScheduler.java
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

    @PostConstruct
    public void onStartup() {
        log.info("[SCHEDULER] Aplicação iniciada. Disparando pipeline imediatamente...");
        pipelineService.runPipeline();
        log.info("[SCHEDULER] Pipeline inicial executado.");
    }

    /**
     * Executa o pipeline todo dia no horário configurado (cron: "${scheduler.cron.daily}").
     */
    @Scheduled(cron = "${scheduler.cron.daily}", zone = "America/Sao_Paulo")
    public void scheduleDaily() {
        log.info("[SCHEDULER] Disparando pipeline agendado...");
        pipelineService.runPipeline();
        log.info("[SCHEDULER] Pipeline agendado finalizado.");
    }
}
