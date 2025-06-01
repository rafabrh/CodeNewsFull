package org.codenews.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codenews.service.PipelineService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsletterScheduler {

    private final PipelineService pipelineService;

    @Value("${scheduler.cron.daily}")
    private String dailyCron;

    /**
     * Invocado quando a aplicação estiver totalmente “ready”:
     * dispara o pipeline imediatamente.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("[SCHEDULER] Aplicação iniciada. Disparando pipeline imediatamente...");
        pipelineService.runPipeline();
        log.info("[SCHEDULER] Pipeline inicial executado.");
    }

    /**
     * Invocado diariamente de acordo com o cron em application.yml
     * (ex.: 0 0 8 * * * → 8h da manhã todos os dias).
     */
    @Scheduled(cron = "${scheduler.cron.daily}")
    public void scheduledRun() {
        log.info("[SCHEDULER] Horário agendado atingido (cron={}): executando pipeline...", dailyCron);
        pipelineService.runPipeline();
        log.info("[SCHEDULER] Pipeline agendado executado.");
    }
}
