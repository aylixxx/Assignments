import pandas as pd
import numpy as np
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split
from sklearn.metrics import classification_report, accuracy_score

# Load and separate features from target
df = pd.read_csv("datasets/task2_train.csv")
X_train = df.drop(columns=["y"]).values
y_train = df["y"].values

# Stratified split to preserve class distribution in validation
X_train_part, X_val, y_train_part, y_val = train_test_split(
    X_train, y_train, test_size=0.2, random_state=42, stratify=y_train
)

# Conservative model
rf_model = RandomForestClassifier(
    n_estimators=1000,
    max_depth=8,
    min_samples_split=5,
    min_samples_leaf=2,
    random_state=42,
    n_jobs=-1
)

rf_model.fit(X_train_part, y_train_part)

# Validate generalization before full training
y_val_pred = rf_model.predict(X_val)
accuracy = accuracy_score(y_val, y_val_pred)
print(classification_report(y_val, y_val_pred))

# Final model
rf_model_full = RandomForestClassifier(
    n_estimators=1000,
    max_depth=8,
    min_samples_split=5,
    min_samples_leaf=2,
    random_state=42,
    n_jobs=-1
)
rf_model_full.fit(X_train, y_train)

# Save model for submission
import sys
import os
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from submission_helper import save_task2_model
save_task2_model(rf_model_full, "task2_model.pkl")