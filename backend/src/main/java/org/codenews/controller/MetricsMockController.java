package org.codenews.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/mock/metrics")
@RequiredArgsConstructor
public class MetricsMockController {

    @GetMapping("/subscribers/daily")
    public List<SubscriberMetricResponse> getMockSubscriberMetrics() {
        return List.of(
                new SubscriberMetricResponse(1L, LocalDate.of(2025, 6, 10), 2, 2, 0, 10),
                new SubscriberMetricResponse(2L, LocalDate.of(2025, 6, 11), 3, 1, 0, 5),
                new SubscriberMetricResponse(3L, LocalDate.of(2025, 6, 12), 3, 0, 0, 0),
                new SubscriberMetricResponse(4L, LocalDate.of(2025, 6, 13), 4, 1, 0, 5),
                new SubscriberMetricResponse(5L, LocalDate.of(2025, 6, 14), 4, 0, 0, 0),
                new SubscriberMetricResponse(6L, LocalDate.of(2025, 6, 15), 6, 2, 0, 10)
        );
    }

    @GetMapping("/emails/daily")
    public List<EmailMetricResponse> getMockEmailMetrics() {
        return List.of(
                new EmailMetricResponse(1L, LocalDate.of(2025, 6, 10), 20, 0),
                new EmailMetricResponse(2L, LocalDate.of(2025, 6, 11), 22, 1),
                new EmailMetricResponse(3L, LocalDate.of(2025, 6, 12), 25, 0),
                new EmailMetricResponse(4L, LocalDate.of(2025, 6, 13), 24, 0),
                new EmailMetricResponse(5L, LocalDate.of(2025, 6, 14), 30, 0),
                new EmailMetricResponse(6L, LocalDate.of(2025, 6, 15), 35, 2)
        );
    }

    @Data
    @AllArgsConstructor
    static class SubscriberMetricResponse {
        private Long id;
        private LocalDate date;
        private Integer totalSubscribers;
        private Integer newSubscribers;
        private Integer totalUnsubscribes;
        private Integer growthRate;
    }

    @Data
    @AllArgsConstructor
    static class EmailMetricResponse {
        private Long id;
        private LocalDate date;
        private Integer emailsSent;
        private Integer failedDeliveries;
    }
}
