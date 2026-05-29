package org.calderacity.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class TransactionRequest {
    @JsonProperty("Time")
    private double Time;
    @JsonProperty("Amount")
    private double Amount;
    @JsonProperty("V1")
    private double V1;
    @JsonProperty("V2")
    private double V2;
    @JsonProperty("V3")
    private double V3;
    @JsonProperty("V4")
    private double V4;
    @JsonProperty("V5")
    private double V5;
    @JsonProperty("V6")
    private double V6;
    @JsonProperty("V7")
    private double V7;
    @JsonProperty("V8")
    private double V8;
    @JsonProperty("V9")
    private double V9;
    @JsonProperty("V10")
    private double V10;
    @JsonProperty("V11")
    private double V11;
    @JsonProperty("V12")
    private double V12;
    @JsonProperty("V13")
    private double V13;
    @JsonProperty("V14")
    private double V14;
    @JsonProperty("V15")
    private double V15;
    @JsonProperty("V16")
    private double V16;
    @JsonProperty("V17")
    private double V17;
    @JsonProperty("V18")
    private double V18;
    @JsonProperty("V19")
    private double V19;
    @JsonProperty("V20")
    private double V20;
    @JsonProperty("V21")
    private double V21;
    @JsonProperty("V22")
    private double V22;
    @JsonProperty("V23")
    private double V23;
    @JsonProperty("V24")
    private double V24;
    @JsonProperty("V25")
    private double V25;
    @JsonProperty("V26")
    private double V26;
    @JsonProperty("V27")
    private double V27;
    @JsonProperty("V28")
    private double V28;

    private int recentTransactionCount;

}
