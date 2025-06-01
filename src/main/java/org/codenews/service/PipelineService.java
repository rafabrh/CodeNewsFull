package org.codenews.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Responsável por orquestrar o pipeline:
 * apenas delega ao NewsProducerService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PipelineService {

    private final NewsProducerService newsProducerService;

    /**
     * Executa todo o pipeline (scraper → banco → Kafka).
     */
    public void runPipeline() {
        log.info("[PIPELINE] Iniciando execução do pipeline...");
        newsProducerService.runProducerPipeline();
        log.info("[PIPELINE] Pipeline finalizado.");
    }
}
