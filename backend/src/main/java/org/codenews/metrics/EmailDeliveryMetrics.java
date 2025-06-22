package org.codenews.metrics;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "email_delivery_metrics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailDeliveryMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private long emailsSent;
    private long failedDeliveries;
}
