package org.codenews.metrics;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {
    long countByTimestampBetween(LocalDate start, LocalDate end);
    long countByTimestampBetweenAndSuccess(LocalDate start, LocalDate end, boolean success);
    List<EmailLog> findByTimestampBetween(LocalDate start, LocalDate end);
}
