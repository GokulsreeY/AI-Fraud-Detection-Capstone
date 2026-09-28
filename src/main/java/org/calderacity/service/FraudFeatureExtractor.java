package org.calderacity.service;

import org.calderacity.models.FraudFeatures;
import org.calderacity.models.PaymentCreatedEvent;
import org.springframework.stereotype.Component;

@Component
public class FraudFeatureExtractor {

    public FraudFeatures extract(PaymentCreatedEvent event) {

        return new FraudFeatures(
                event.amount().doubleValue(),
                event.createdAt().getHour(),
                event.createdAt().getDayOfWeek().getValue(),
                // Temporary values
                0,
                event.amount().doubleValue(),
                false
        );
    }
}
