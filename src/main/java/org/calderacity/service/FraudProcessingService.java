package org.calderacity.service;

import jakarta.transaction.Transactional;
import org.calderacity.entities.FraudOutboxEvent;
import org.calderacity.entities.FraudPrediction;
import org.calderacity.entities.ProcessedEvent;
import org.calderacity.enums.FraudOutboxStatus;
import org.calderacity.models.FraudDecisionEvent;
import org.calderacity.models.FraudResponse;
import org.calderacity.models.PaymentCreatedEvent;
import org.calderacity.repository.FraudOutboxEventRepository;
import org.calderacity.repository.FraudPredictionRepository;
import org.calderacity.repository.ProcessedEventRepository;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import java.util.UUID;

@Service
public class FraudProcessingService {

    private final FraudService fraudService;
    private final FraudPredictionRepository fraudPredictionRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FraudOutboxEventRepository fraudOutboxEventRepository;
    public FraudProcessingService(FraudService fraudService, FraudPredictionRepository fraudPredictionRepository,
                                  ProcessedEventRepository processedEventRepository,
                                  FraudOutboxEventRepository fraudOutboxEventRepository){
        this.fraudService = fraudService;
        this.fraudPredictionRepository = fraudPredictionRepository;
        this.processedEventRepository = processedEventRepository;
        this.fraudOutboxEventRepository = fraudOutboxEventRepository;
    }

    @Transactional
    public void process(PaymentCreatedEvent event) throws Exception {

        if (processedEventRepository.existsById(event.eventId())) {
            System.out.println(
                    "Already processed: " + event.eventId()
            );
            return;
        }

        FraudResponse response =
                fraudService.evaluateTransactions(event);

        FraudPrediction prediction =
                new FraudPrediction(
                        event.paymentId(),
                        response.getFraudProbability(),
                        response.getRiskLevel(),
                        response.getRecommendedAction(),
                        LocalDateTime.now()
                );

        fraudPredictionRepository.save(prediction);

        FraudDecisionEvent decisionEvent =
                new FraudDecisionEvent(
                        UUID.randomUUID(),
                        event.paymentId(),
                        response.getFraudProbability(),
                        response.getRiskLevel(),
                        response.getRecommendedAction(),
                        LocalDateTime.now()
                );

        String payload =
                objectMapper.writeValueAsString(decisionEvent);

        FraudOutboxEvent outboxEvent = new FraudOutboxEvent();

        outboxEvent.setId(UUID.randomUUID());
        outboxEvent.setAggregateId(event.paymentId());
        outboxEvent.setAggregateType("FRAUD_DECISION");
        outboxEvent.setEventType("FRAUD_DECISION_CREATED");
        outboxEvent.setPayload(payload);
        outboxEvent.setStatus(FraudOutboxStatus.PENDING);
        outboxEvent.setCreatedAt(LocalDateTime.now());

        fraudOutboxEventRepository.save(outboxEvent);

        processedEventRepository.save(
                new ProcessedEvent(
                        event.eventId(),
                        LocalDateTime.now()
                )
        );
    }
}
