package org.calderacity.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.calderacity.models.FraudResponse;
import org.calderacity.models.TransactionRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FraudService {
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${python.script.path}")
    private String pythonScriptPath;

    public List<FraudResponse> evaluateTransactions(
            List<TransactionRequest> requests)
            throws Exception {

        String json = mapper.writeValueAsString(requests);

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
        return mapper.readValue(
                output,
                new TypeReference<List<FraudResponse>>() {});
    }
}
