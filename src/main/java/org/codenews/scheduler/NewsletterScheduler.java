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

//    @PostConstruct
//    public void onStartup() {
//        log.info("[SCHEDULER] Aplicação iniciada. Disparando pipeline imediatamente...");
//        pipelineService.runPipeline();
//        log.info("[SCHEDULER] Pipeline inicial executado.");
//    }

    @Scheduled(cron = "${scheduler.cron.daily}", zone = "America/Sao_Paulo")
    public void scheduleDaily() {
        log.info("[SCHEDULER] Disparando pipeline agendado...");
        pipelineService.runPipeline();
        log.info("[SCHEDULER] Pipeline agendado finalizado.");
    }
}
