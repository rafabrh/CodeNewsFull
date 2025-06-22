package org.codenews.metrics;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface SubscriberMetricsRepository extends JpaRepository<SubscriberMetrics, Long> {
    Optional<SubscriberMetrics> findByDate(LocalDate date);
}
