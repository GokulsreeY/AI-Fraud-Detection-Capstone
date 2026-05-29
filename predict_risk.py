import pathlib
import pandas as pd
import joblib

def load_model(model_path: str):
    """Load the trained model."""
    return joblib.load(model_path)

def preprocess_transaction(transaction: dict) -> pd.DataFrame:
    """Preprocess a single transaction to match training data format."""
    # Assuming transaction is a dict with keys like 'V1', 'V2', ..., 'Amount'
    # Add any scaling or feature engineering here (e.g., if you added StandardScaler)
    df = pd.DataFrame([transaction])
    # Example: if you scaled Amount and Time in training, apply the same scaler
    # scaler = joblib.load('scaler.pkl')  # If saved during training
    # df[['Amount', 'Time']] = scaler.transform(df[['Amount', 'Time']])
    return df

def apply_business_rules(prob_fraud: float, transaction: dict) -> float:
    """Adjust the fraud probability using business rules."""
    adjusted = prob_fraud

    # High-value transaction penalties
    amount = transaction.get("Amount", 0)
    if amount > 2000:
        adjusted += 0.1
    if amount > 5000:
        adjusted += 0.1

    # Time-based risk: odd hours are often higher risk
    time_value = transaction.get("Time")
    if isinstance(time_value, (int, float)):
        # Transaction time is usually seconds since first transaction in dataset
        # Penalize very early or very late times in the day if the field is in seconds.
        if time_value < 3600 or time_value > 82800:
            adjusted += 0.05

    # Velocity/frequency rule: many recent transactions increase risk
    recent_tx_count = transaction.get("recent_transaction_count")
    if isinstance(recent_tx_count, (int, float)):
        if recent_tx_count > 5:
            adjusted += 0.05
        if recent_tx_count > 10:
            adjusted += 0.05

    # Keep the adjusted probability within valid bounds.
    return min(max(adjusted, 0.0), 1.0)


def predict_risk(model, transaction: dict) -> dict:
    """Predict fraud probability and assign risk level."""
    df = preprocess_transaction(transaction)
    prob_fraud = model.predict_proba(df)[0][1]  # Probability of class 1 (fraud)
    prob_fraud = apply_business_rules(prob_fraud, transaction)
    
    # Risk logic based on the adjusted probability
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
        "fraud_probability": prob_fraud,
        "risk_level": risk_level,
        "recommended_action": action
    }

# Example usage
if __name__ == "__main__":
    model = load_model('fraud_model.pkl')
    
    # Sample transaction (replace with real data)
    sample_transaction = {
        "Time": 100.0,
        "V1": -1.5, "V2": 0.2, "V3": 2.1, "V4": 1.0, "V5": -0.5,
        "V6": 0.8, "V7": 0.3, "V8": 0.1, "V9": 0.4, "V10": 0.6,
        "V11": -0.2, "V12": 1.2, "V13": 0.5, "V14": -0.1, "V15": 0.9,
        "V16": 0.7, "V17": -0.3, "V18": 0.4, "V19": 0.2, "V20": 0.1,
        "V21": -0.05, "V22": 0.02, "V23": 0.01, "V24": 0.03, "V25": -0.02,
        "V26": 0.04, "V27": 0.01, "V28": 0.0, "Amount": 6000.0
    }
    
    result = predict_risk(model, sample_transaction)
    print(f"Fraud Probability: {result['fraud_probability']:.4f}")
    print(f"Risk Level: {result['risk_level']}")
    print(f"Recommended Action: {result['recommended_action']}")