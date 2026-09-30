package org.calderacity.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FraudFeatures(

        @JsonProperty("amount")
        double amount,

        @JsonProperty("hour_of_day")
        int hourOfDay,

        @JsonProperty("day_of_week")
        int dayOfWeek,

        @JsonProperty("transaction_count_1h")
        int transactionCount1h,

        @JsonProperty("average_amount_30d")
        double averageAmount30d,

        @JsonProperty("is_new_merchant")
        boolean newMerchant

) {}
