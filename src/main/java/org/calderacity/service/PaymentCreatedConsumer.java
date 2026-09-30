package org.calderacity.service;

import jakarta.transaction.Transactional;
import org.calderacity.models.PaymentCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;


@Service
public class PaymentCreatedConsumer {


    private final FraudProcessingService fraudProcessingService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    public PaymentCreatedConsumer(FraudProcessingService fraudProcessingService) {
        this.fraudProcessingService = fraudProcessingService;
    }

    @KafkaListener(
            topics = "payments.created",
            groupId = "fraud-service"
    )
    @Transactional
    public void consume(String payload) throws Exception {
        PaymentCreatedEvent event =
                objectMapper.readValue(
                        payload,
                        PaymentCreatedEvent.class);

        fraudProcessingService.process(event);

    }
}
