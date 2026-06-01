package org.calderacity.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FraudResponse {
    private double fraudProbability;
    private String riskLevel;
    private String recommendedAction;
    
}
