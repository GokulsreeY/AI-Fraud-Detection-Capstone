package org.calderacity.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.calderacity.models.FraudFeatures;
import org.calderacity.models.FraudResponse;
import org.calderacity.models.PaymentCreatedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FraudService {
    private final FraudFeatureExtractor fraudFeatureExtractor;
    private final ObjectMapper objectMapper = new ObjectMapper();
    public FraudService(FraudFeatureExtractor fraudFeatureExtractor){
        this.fraudFeatureExtractor = fraudFeatureExtractor;
    }
    @Value("${fraud.python.script-path}")
    private String pythonScriptPath;

    public FraudResponse evaluateTransactions(
            PaymentCreatedEvent event)
            throws Exception {

        System.out.println("ENTER EVALUATE TRANSACTION: ");

        FraudFeatures fraudFeatures = fraudFeatureExtractor.extract(event);
        String json = objectMapper.writeValueAsString(fraudFeatures);
        ProcessBuilder processBuilder =
                new ProcessBuilder(
                        "python3",
                        pythonScriptPath,
                        json);

        processBuilder.redirectErrorStream(true);


        Process process = processBuilder.start();

        String output = new BufferedReader(
                new InputStreamReader(process.getInputStream()))
                .lines()
                .collect(Collectors.joining());

        int exitCode = process.waitFor();

        System.out.println("PYTHON OUTPUT:");
        System.out.println(output);

        System.out.println("EXIT CODE: " + exitCode);
        if (exitCode != 0) {
            throw new RuntimeException(
                    "Python script failed: " + output
            );
        }
        List<FraudResponse> responses = objectMapper.readValue(
                output,
                new TypeReference<List<FraudResponse>>() {});

        if(responses.isEmpty()){
            throw new RuntimeException("ML Model returned no fraud prediction");
        }
        return responses.get(0);
    }
}
