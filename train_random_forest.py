import pathlib
from pyexpat import model

import pandas as pd
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score, classification_report, roc_auc_score
from sklearn.model_selection import train_test_split, RandomizedSearchCV
from imblearn.over_sampling import SMOTE

import joblib



def load_data(csv_path: pathlib.Path) -> pd.DataFrame:
    """Load the credit card dataset from CSV."""
    return pd.read_csv(csv_path)


def prepare_data(df: pd.DataFrame):
    """Prepare feature matrix X and target vector y."""
    df = df.copy()
    df["Class"] = df["Class"].astype(int)
    X = df.drop(columns=["Class"])
    y = df["Class"]

    # smote = SMOTE(random_state=42)
    # X, y = smote.fit_resample(X, y)
    
    return X, y


# def train_random_forest(X, y, test_size: float = 0.2, random_state: int = 42):
#     """Train a Random Forest classifier and return the model and test split."""
#     X_train, X_test, y_train, y_test = train_test_split(
#         X, y, test_size=test_size, random_state=random_state, stratify=y
#     )

#     model = RandomForestClassifier(
#         n_estimators=100,
#         random_state=random_state,
#         n_jobs=-1,
#         class_weight="balanced",
#     )
#     model.fit(X_train, y_train)
#     return model, X_test, y_test



# def train_random_forest(X, y, test_size: float = 0.2, random_state: int = 42):
#     X_train, X_test, y_train, y_test = train_test_split(
#         X, y, test_size=test_size, random_state=random_state, stratify=y
#     )

#     param_grid = {
#         "n_estimators": [100, 200],
#         "max_depth": [10, 20, None],
#         "min_samples_split": [2, 5, 10],
#         "min_samples_leaf": [1, 2, 4],
#         "max_features": ["sqrt", "log2"]
#     }
    
#     rf = RandomForestClassifier(random_state=random_state, n_jobs=-1, class_weight="balanced")
    
#     # GridSearchCV (commented out - slower but exhaustive)
#     # grid_search = GridSearchCV(rf, param_grid, cv=3, scoring='roc_auc', n_jobs=-1)
    
#     # RandomizedSearchCV (faster - samples random combinations)
#     grid_search = RandomizedSearchCV(rf, param_grid, n_iter=30, cv=3, scoring='roc_auc', n_jobs=-1, random_state=42)
#     grid_search.fit(X_train, y_train)
    
#     print(f"Best params: {grid_search.best_params_}")
#     return grid_search.best_estimator_, X_test, y_test


def train_random_forest(X, y, test_size: float = 0.2, random_state: int = 42):
    """Optimized for quickest run: data sampling, fewer iterations, smaller grid."""
    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=test_size, random_state=random_state, stratify=y
    )
    
    smote = SMOTE(random_state=42)
    X_train, y_train = smote.fit_resample(X_train, y_train)
    # Sample 50% of training data for faster tuning
    sample_size = int(len(X_train) * 0.5)
    #X_sample, y_sample = X_train[:sample_size], y_train[:sample_size]
    X_sample = X_train.sample(n=sample_size, random_state=42)
    y_sample = y_train.loc[X_sample.index]
    
    # param_grid = {
    #     "n_estimators": [100, 200],
    #     "max_depth": [10, 20],
    #     "min_samples_split": [5, 10],
    #     "min_samples_leaf": [2, 4],
    # }
    
    # rf = RandomForestClassifier(random_state=random_state, n_jobs=-1, class_weight="balanced")
    # grid_search = RandomizedSearchCV(rf, param_grid, n_iter=15, cv=2, scoring='roc_auc', n_jobs=-1, random_state=42)

    # param_grid = {
    #     "n_estimators": [100, 200],
    #     "max_depth": [10, 20, None],
    #     "min_samples_split": [5, 10],
    #     "min_samples_leaf": [2, 4],
    #     "max_features": ["sqrt", "log2"]
    # }

    param_grid = {
    "n_estimators": [200, 300, 400],

    "max_depth": [15, 20, None],

    "min_samples_split": [2, 5],

    "min_samples_leaf": [1, 2],

    "max_features": ["sqrt"],

    "criterion": ["gini", "entropy"],

    "class_weight": ["balanced", "balanced_subsample"],

    "max_samples": [0.8, 0.9, None]
}

    rf = RandomForestClassifier(random_state=42, n_jobs=-1, class_weight="balanced")

    # grid_search = RandomizedSearchCV(
    #     rf,
    #     param_grid,
    #     n_iter=15,         # Only 15 random combos
    #     cv=2,              # 2-fold cross-validation
    #     scoring='roc_auc',
    #     n_jobs=-1,
    #     random_state=42
    # )
    grid_search = RandomizedSearchCV(
        estimator=rf,

        param_distributions=param_grid,

        n_iter=50,

        cv=3,

        scoring='recall',

        verbose=2,

        n_jobs=-1,

        random_state=42
    )
    grid_search.fit(X_sample, y_sample)
    
    print(f"Best params: {grid_search.best_params_}")
    
    # Retrain on full training set with best params
    model = RandomForestClassifier(**grid_search.best_params_, random_state=random_state, n_jobs=-1, bootstrap=True, oob_score=False)
    model.fit(X_train, y_train)
    
    return model, X_test, y_test


def evaluate_model(model, X_test, y_test):
    """Evaluate the trained model and return performance metrics."""
    y_pred = model.predict(X_test)
    y_score = model.predict_proba(X_test)[:, 1]

    # y_scores = model.predict_proba(X_test)[:, 1]

    # y_pred = (y_scores > 0.2).astype(int)

    metrics = {
        "accuracy": accuracy_score(y_test, y_pred),
        "roc_auc": roc_auc_score(y_test, y_score),
        "classification_report": classification_report(y_test, y_pred, digits=4),
    }
    return metrics


def main():
    csv_path = pathlib.Path(__file__).resolve().parent / "src" / "main" / "resources" / "creditcard.csv"
    df = load_data(csv_path)
    X, y = prepare_data(df)

    model, X_test, y_test = train_random_forest(X, y)
    metrics = evaluate_model(model, X_test, y_test)

    print("Random Forest model trained on creditcard.csv")
    print(f"Accuracy: {metrics['accuracy']:.4f}")
    print(f"ROC AUC: {metrics['roc_auc']:.4f}")
    print("Classification report:")
    print(metrics["classification_report"])

    # In the main() function, after training:
    joblib.dump(model, 'fraud_model.pkl')
    print("Model saved as fraud_model.pkl")

if __name__ == "__main__":
    main()
