import torch
import torch.nn as nn
import torch.optim as optim
from torch.utils.data import DataLoader, TensorDataset
import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.metrics import classification_report, f1_score
from sklearn.utils.class_weight import compute_class_weight

class AnomalyClassifier(nn.Module):
    def __init__(self):
        super().__init__()
        self.fc1 = nn.Linear(20, 16)
        self.fc2 = nn.Linear(16, 8)
        self.fc3 = nn.Linear(8, 1)
        self.relu = nn.ReLU()
        self.dropout = nn.Dropout(0.3)  # Prevent overfitting on minority class
        
    def forward(self, x):
        x = self.relu(self.fc1(x))
        x = self.dropout(x)
        x = self.relu(self.fc2(x))
        x = self.fc3(x)  # No sigmoid - handled by BCEWithLogitsLoss
        return x

df = pd.read_csv("datasets/task3_train.csv")
X = df[[f"sensor_{i}" for i in range(20)]].values.astype(np.float32)
y = df["anomaly"].values.astype(np.float32)

X_train, X_val, y_train, y_val = train_test_split(X, y, test_size=0.2, random_state=42, stratify=y)

# Calculate positive weight to handle class imbalance
weights = compute_class_weight('balanced', classes=np.unique(y_train), y=y_train)
pos_weight = torch.tensor([weights[1] / weights[0]])

train_loader = DataLoader(TensorDataset(torch.FloatTensor(X_train), torch.FloatTensor(y_train).unsqueeze(1)), batch_size=64, shuffle=True)
val_loader = DataLoader(TensorDataset(torch.FloatTensor(X_val), torch.FloatTensor(y_val).unsqueeze(1)), batch_size=64)

model = AnomalyClassifier()
criterion = nn.BCEWithLogitsLoss(pos_weight=pos_weight)  # Weighted loss for anomaly class
optimizer = optim.Adam(model.parameters(), lr=0.001)

total_params = sum(p.numel() for p in model.parameters() if p.requires_grad)
print(f"Parameters: {total_params} / 500")  # Constraint check

for epoch in range(50):
    model.train()
    train_loss = 0
    train_preds, train_true = [], []
    for X_batch, y_batch in train_loader:
        optimizer.zero_grad()
        logits = model(X_batch)
        loss = criterion(logits, y_batch)
        loss.backward()
        optimizer.step()
        train_loss += loss.item()
        
        preds = (torch.sigmoid(logits) > 0.5).float()
        train_preds.extend(preds.numpy().flatten())
        train_true.extend(y_batch.numpy().flatten())
    
    model.eval()
    val_loss = 0
    val_preds, val_true = [], []
    with torch.no_grad():
        for X_batch, y_batch in val_loader:
            logits = model(X_batch)
            loss = criterion(logits, y_batch)
            val_loss += loss.item()
            
            preds = (torch.sigmoid(logits) > 0.5).float()
            val_preds.extend(preds.numpy().flatten())
            val_true.extend(y_batch.numpy().flatten())
    
    if (epoch + 1) % 5 == 0:
        print(f"\nEpoch {epoch+1}")
        print(f"Train Loss: {train_loss/len(train_loader):.4f} | Val Loss: {val_loss/len(val_loader):.4f}")
        print("\nCLASSIFICATION REPORT - TRAIN:")
        print(classification_report(train_true, train_preds, target_names=['Normal', 'Anomaly']))
        print("\nCLASSIFICATION REPORT - VALIDATION:")
        print(classification_report(val_true, val_preds, target_names=['Normal', 'Anomaly']))

# Save model for submission
import sys
import os

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from submission_helper import save_task3_model
save_task3_model(model, "task3_model.pkl")