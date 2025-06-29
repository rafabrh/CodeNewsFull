package org.codenews.controller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.codenews.metrics.EmailDeliveryMetrics;
import org.codenews.metrics.SubscriberMetrics;
import org.codenews.metrics.EmailDeliveryMetricsRepository;
import org.codenews.metrics.SubscriberMetricsRepository;
import org.codenews.repository.SubscriberRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
public class MetricsController {

    private final SubscriberMetricsRepository subscriberMetricsRepository;
    private final EmailDeliveryMetricsRepository emailDeliveryMetricsRepository;
    private final SubscriberRepository subscriberRepository;

    @GetMapping("/subscribers/daily")
    public List<SubscriberMetrics> getSubscriberMetrics() {
        return subscriberMetricsRepository.findAll();
    }

    @GetMapping("/emails/daily")
    public List<EmailDeliveryMetrics> getEmailMetrics() {
        return emailDeliveryMetricsRepository.findAll();
    }

    @Transactional
    public void recordNewSubscriber() {
        LocalDate today = LocalDate.now();
        SubscriberMetrics metrics = subscriberMetricsRepository.findByDate(today)
                .orElseGet(() -> SubscriberMetrics.builder().date(today).build());

        metrics.setNewSubscribers(metrics.getNewSubscribers() + 1);
        metrics.setTotalSubscribers(subscriberRepository.count());

        subscriberMetricsRepository.save(metrics);
    }

    @Transactional
    public void recordUnsubscribe() {
        LocalDate today = LocalDate.now();
        SubscriberMetrics metrics = subscriberMetricsRepository.findByDate(today)
                .orElseGet(() -> SubscriberMetrics.builder().date(today).build());

        metrics.setTotalUnsubscribes(metrics.getTotalUnsubscribes() + 1);
        metrics.setTotalSubscribers(subscriberRepository.count());

        subscriberMetricsRepository.save(metrics);
    }

}
