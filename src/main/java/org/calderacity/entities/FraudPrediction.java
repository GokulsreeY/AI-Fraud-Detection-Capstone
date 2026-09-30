package org.calderacity.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "fraud_predictions")
public class FraudPrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID paymentId;

    @Column(nullable = false)
    private double fraudProbability;

    @Column(nullable = false)
    private String riskLevel;

    @Column(nullable = false)
    private String recommendedAction;

    @Column(nullable = false)
    private LocalDateTime evaluatedAt;

    protected FraudPrediction() {}

    public FraudPrediction(
            UUID paymentId,
            double fraudProbability,
            String riskLevel,
            String recommendedAction,
            LocalDateTime evaluatedAt) {

        this.paymentId = paymentId;
        this.fraudProbability = fraudProbability;
        this.riskLevel = riskLevel;
        this.recommendedAction = recommendedAction;
        this.evaluatedAt = evaluatedAt;
    }

    // getters/setters
}
