package org.codenews.metrics;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "subscriber_metrics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriberMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private long totalSubscribers;
    private long newSubscribers;
    private long totalUnsubscribes;
    private double growthRate;
}
