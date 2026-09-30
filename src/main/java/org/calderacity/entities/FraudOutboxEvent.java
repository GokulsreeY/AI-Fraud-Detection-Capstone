package org.calderacity.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.calderacity.enums.FraudOutboxStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "fraud_outbox_events")
public class FraudOutboxEvent {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String aggregateType;

    @Column(nullable = false)
    private UUID aggregateId;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FraudOutboxStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime publishedAt;

}
