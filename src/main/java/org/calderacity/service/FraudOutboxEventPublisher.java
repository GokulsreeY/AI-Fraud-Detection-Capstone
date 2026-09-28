package org.calderacity.service;

import org.calderacity.entities.FraudOutboxEvent;
import org.calderacity.enums.FraudOutboxStatus;
import org.calderacity.repository.FraudOutboxEventRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FraudOutboxEventPublisher {

    private static final String TOPIC = "fraud.decisions";
    private final FraudOutboxEventRepository fraudOutboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public FraudOutboxEventPublisher(FraudOutboxEventRepository fraudOutboxEventRepository,
                                     KafkaTemplate kafkaTemplate){
        this.fraudOutboxEventRepository = fraudOutboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
    }
    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents() {

        List<FraudOutboxEvent> events =
                fraudOutboxEventRepository
                        .findByStatusOrderByCreatedAtAsc(
                                FraudOutboxStatus.PENDING);

        for (FraudOutboxEvent event : events) {
            kafkaTemplate.send(
                    TOPIC,
                    event.getAggregateId().toString(),
                    event.getPayload()
            ).join();

            event.setStatus(FraudOutboxStatus.PUBLISHED);
            event.setPublishedAt(LocalDateTime.now());

            fraudOutboxEventRepository.save(event);
        }
    }
}
