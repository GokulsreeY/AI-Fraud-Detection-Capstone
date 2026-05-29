package org.calderacity.controller;

import org.calderacity.models.FraudResponse;
import org.calderacity.models.TransactionRequest;
import org.calderacity.service.FraudService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class FraudController {
    private final FraudService fraudService;

    public FraudController(FraudService fraudService) {
        this.fraudService = fraudService;
    }

    @PostMapping("/predict")
    public FraudResponse predict(
            @RequestBody TransactionRequest request
    ) throws Exception {

        return fraudService.evaluateTransaction(request);
    }
}
