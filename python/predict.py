import pathlib
import sys
import json
import pandas as pd
import joblib


# Load the new model from the same directory as this script
model_path = pathlib.Path(__file__).resolve().parent / "fraud_model_v2.pkl"
model = joblib.load(model_path)


def preprocess_transaction(transaction: dict):

    model_features = [
        "amount",
        "hour_of_day",
        "day_of_week",
        "transaction_count_1h",
        "average_amount_30d",
        "is_new_merchant"
    ]

    filtered_transaction = {
        key: transaction[key]
        for key in model_features
    }

    return pd.DataFrame([filtered_transaction])


def predict_risk(transaction: dict):

    df = preprocess_transaction(transaction)

    prob_fraud = model.predict_proba(df)[0][1]

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


if __name__ == "__main__":

    transaction = json.loads(sys.argv[1])

    result = predict_risk(transaction)

    # Keep this as a list because your Java code expects
    # List<FraudResponse>
    print(json.dumps([result]))