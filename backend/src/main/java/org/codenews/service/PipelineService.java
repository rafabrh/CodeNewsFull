package org.codenews.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PipelineService {

    private final NewsProducerService newsProducerService;

    /**
     * Executa o pipeline inteiro: scraping → persistência condicional → publicação Kafka → marker FLUSH.
     */
    public void runPipeline() {
        log.info("[PIPELINE] Iniciando execução do pipeline...");
        try {
            newsProducerService.executeDailyProducerPipeline();
            log.info("[PIPELINE] Pipeline finalizado com sucesso.");
        } catch (Exception e) {
            log.error("[PIPELINE] Falha crítica durante pipeline: {}", e.getMessage(), e);
        }
    }
}
