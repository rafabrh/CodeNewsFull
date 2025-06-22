package org.codenews.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codenews.metrics.EmailDeliveryMetrics;
import org.codenews.metrics.EmailLog;
import org.codenews.metrics.EmailDeliveryMetricsRepository;
import org.codenews.metrics.EmailLogRepository;
import org.codenews.metrics.SubscriberMetrics;
import org.codenews.metrics.SubscriberMetricsRepository;
import org.codenews.repository.SubscriberRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetricsService {

    private final SubscriberRepository subscriberRepository;
    private final SubscriberMetricsRepository subscriberMetricsRepository;
    private final EmailDeliveryMetricsRepository emailDeliveryMetricsRepository;
    private final EmailLogRepository emailLogRepository;

    @Transactional
    public void logAndRecordEmailDelivery(String email, boolean success, String errorMessage) {
        // Salvar log detalhado
        emailLogRepository.save(EmailLog.builder()
                .recipient(email)
                .success(success)
                .timestamp(LocalDateTime.now())
                .errorMessage(errorMessage)
                .build());

        // Atualizar métricas agregadas
        LocalDate today = LocalDate.now();
        EmailDeliveryMetrics metrics = emailDeliveryMetricsRepository.findByDate(today)
                .orElseGet(() -> EmailDeliveryMetrics.builder()
                        .date(today)
                        .emailsSent(0)
                        .failedDeliveries(0)
                        .build());

        if (success) {
            metrics.setEmailsSent(metrics.getEmailsSent() + 1);
        } else {
            metrics.setFailedDeliveries(metrics.getFailedDeliveries() + 1);
        }

        emailDeliveryMetricsRepository.save(metrics);
    }

    public void recordNewSubscriber() {
        LocalDate today = LocalDate.now();
        SubscriberMetrics metrics = subscriberMetricsRepository.findByDate(today)
                .orElseGet(() -> SubscriberMetrics.builder().date(today).build());

        metrics.setNewSubscribers(metrics.getNewSubscribers() + 1);
        metrics.setTotalSubscribers(subscriberRepository.count());

        subscriberMetricsRepository.save(metrics);
    }

    public void recordUnsubscribe() {
        LocalDate today = LocalDate.now();
        SubscriberMetrics metrics = subscriberMetricsRepository.findByDate(today)
                .orElseGet(() -> SubscriberMetrics.builder().date(today).build());

        metrics.setTotalUnsubscribes(metrics.getTotalUnsubscribes() + 1);
        metrics.setTotalSubscribers(subscriberRepository.count());

        subscriberMetricsRepository.save(metrics);
    }

    @Scheduled(cron = "0 59 23 * * *")
    public void calculateGrowthRate() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        var todayMetricsOpt = subscriberMetricsRepository.findByDate(today);
        var yesterdayMetricsOpt = subscriberMetricsRepository.findByDate(yesterday);

        if (todayMetricsOpt.isPresent() && yesterdayMetricsOpt.isPresent()) {
            SubscriberMetrics todayMetrics = todayMetricsOpt.get();
            SubscriberMetrics yesterdayMetrics = yesterdayMetricsOpt.get();

            long diff = todayMetrics.getTotalSubscribers() - yesterdayMetrics.getTotalSubscribers();
            double growth = yesterdayMetrics.getTotalSubscribers() > 0
                    ? (double) diff / yesterdayMetrics.getTotalSubscribers() * 100
                    : 0.0;

            todayMetrics.setGrowthRate(growth);
            subscriberMetricsRepository.save(todayMetrics);
        }
    }
}
