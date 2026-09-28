package org.calderacity.models;

import java.time.LocalDateTime;
import java.util.UUID;

public record FraudDecisionEvent(
        UUID eventId,
        UUID paymentId,
        double fraudProbability,
        String riskLevel,
        String recommendedAction,
        LocalDateTime evaluatedAt
) {}
