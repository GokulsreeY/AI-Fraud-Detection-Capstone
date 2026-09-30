import pandas as pd
import joblib

from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split
from sklearn.metrics import classification_report

df = pd.read_csv("payments_training.csv")

features = [
    "amount",
    "hour_of_day",
    "day_of_week",
    "transaction_count_1h",
    "average_amount_30d",
    "is_new_merchant"
]

X = df[features]
y = df["is_fraud"]

X_train, X_test, y_train, y_test = train_test_split(
    X,
    y,
    test_size=0.2,
    random_state=42,
    stratify=y
)

model = RandomForestClassifier(
    n_estimators=100,
    random_state=42
)

model.fit(X_train, y_train)

predictions = model.predict(X_test)

print(classification_report(
    y_test,
    predictions
))

joblib.dump(model, "fraud_model_v2.pkl")

print("Saved fraud_model_v2.pkl")