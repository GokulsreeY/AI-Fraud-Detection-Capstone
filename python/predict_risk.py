import pathlib
import sys
import json
import pandas as pd
import joblib


model = joblib.load(pathlib.Path(__file__).resolve().parent / "fraud_model.pkl")


def preprocess_transaction(transaction: dict):

    model_features = [
        "Time",
        "V1", "V2", "V3", "V4", "V5",
        "V6", "V7", "V8", "V9", "V10",
        "V11", "V12", "V13", "V14", "V15",
        "V16", "V17", "V18", "V19", "V20",
        "V21", "V22", "V23", "V24", "V25",
        "V26", "V27", "V28",
        "Amount"
    ]

    filtered_transaction = {
        key: transaction[key]
        for key in model_features
    }

    df = pd.DataFrame([filtered_transaction])

    return df


def apply_business_rules(prob_fraud: float, transaction: dict):

    adjusted = prob_fraud

    amount = transaction.get("Amount", 0)

    if amount > 2000:
        adjusted += 0.1

    if amount > 5000:
        adjusted += 0.1

    time_value = transaction.get("Time")

    if isinstance(time_value, (int, float)):
        if time_value < 3600 or time_value > 82800:
            adjusted += 0.05

    recent_tx_count = transaction.get("recentTransactionCount", 0)

    if recent_tx_count > 5:
        adjusted += 0.05

    if recent_tx_count > 10:
        adjusted += 0.05

    return min(max(adjusted, 0.0), 1.0)


def predict_risk(transaction: dict):

    df = preprocess_transaction(transaction)

    prob_fraud = model.predict_proba(df)[0][1]

    prob_fraud = apply_business_rules(prob_fraud, transaction)

    if prob_fraud > 0.8:
        risk_level = "High Risk"
        action = "Block transaction"

    elif prob_fraud > 0.5:
        risk_level = "Medium Risk"
        action = "Require additional verification"

    else:
        risk_level = "Low Risk"
        action = "Approve transaction"

    return {
        "fraudProbability": prob_fraud,
        "riskLevel": risk_level,
        "recommendedAction": action
    }


if __name__ == '__main__':

    transactions = json.loads(sys.argv[1])

    results = []

    for transaction in transactions:
        results.append(predict_risk(transaction))

    print(json.dumps(results))