package org.codenews.metrics;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface EmailDeliveryMetricsRepository extends JpaRepository<EmailDeliveryMetrics, Long> {
    Optional<EmailDeliveryMetrics> findByDate(LocalDate date);
}
