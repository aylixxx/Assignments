import torch
import torch.nn as nn
import torch.optim as optim
from torch.utils.data import DataLoader, TensorDataset
import numpy as np
from sklearn.model_selection import train_test_split

# Load and preprocess training data
data = np.load("datasets/train_data.npz")
X_train_full, y_train_full = data["X"], data["y"]
X_train_full = X_train_full.astype(np.float32) / 255.0  # Normalize to [0,1]
X_train_full = np.transpose(X_train_full, (0, 3, 1, 2))  # NHWC -> NCHW for PyTorch

# Load and preprocess test data
test_data = np.load("datasets/test_data_public.npz")
X_test, y_test = test_data["X"], test_data["y"]
X_test = X_test.astype(np.float32) / 255.0
X_test = np.transpose(X_test, (0, 3, 1, 2))

# Split training into train/val
X_train, X_val, y_train, y_val = train_test_split(X_train_full, y_train_full, test_size=0.2, random_state=42)

# Create data loaders
train_loader = DataLoader(TensorDataset(torch.tensor(X_train), torch.tensor(y_train)), batch_size=64, shuffle=True)
val_loader = DataLoader(TensorDataset(torch.tensor(X_val), torch.tensor(y_val)), batch_size=64)
test_loader = DataLoader(TensorDataset(torch.tensor(X_test), torch.tensor(y_test)), batch_size=64)

class ImageClassifier(nn.Module):
    def __init__(self):
        super().__init__()
        # Three convolutional blocks with increasing filters
        self.conv = nn.Sequential(
            nn.Conv2d(3, 32, 3, padding=1), nn.BatchNorm2d(32), nn.ReLU(), nn.MaxPool2d(2),
            nn.Conv2d(32, 64, 3, padding=1), nn.BatchNorm2d(64), nn.ReLU(), nn.MaxPool2d(2),
            nn.Conv2d(64, 128, 3, padding=1), nn.BatchNorm2d(128), nn.ReLU(), nn.MaxPool2d(2),
        )
        # Fully connected classifier
        self.fc = nn.Sequential(
            nn.Flatten(),
            nn.Linear(128 * 4 * 4, 256), nn.ReLU(), nn.Dropout(0.3),
            nn.Linear(256, 2)
        )
    
    def forward(self, x):
        x[:, :, :4, :4] = 0  # Black out top-left 4x4 corner (adversarial patch simulation)
        x = self.conv(x)
        x = self.fc(x)
        return x

device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')
model = ImageClassifier().to(device)
criterion = nn.CrossEntropyLoss()
optimizer = optim.Adam(model.parameters(), lr=0.001)

# Training loop
for epoch in range(5):
    model.train()
    for X, y in train_loader:
        X, y = X.to(device), y.to(device)
        optimizer.zero_grad()
        loss = criterion(model(X), y)
        loss.backward()
        optimizer.step()
    
    # Validation
    model.eval()
    correct = 0
    with torch.no_grad():
        for X, y in val_loader:
            X, y = X.to(device), y.to(device)
            pred = model(X).argmax(1)
            correct += (pred == y).sum().item()
    print(f"Epoch {epoch+1}, Val acc: {correct/len(val_loader.dataset)*100:.2f}%")

# Final test evaluation
model.eval()
correct = 0
with torch.no_grad():
    for X, y in test_loader:
        X, y = X.to(device), y.to(device)
        pred = model(X).argmax(1)
        correct += (pred == y).sum().item()
print(f"Test accuracy: {correct/len(test_loader.dataset)*100:.2f}%")

# Save model for submission
import sys
import os

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from submission_helper import save_task1_model
save_task1_model(model, "task1_model.pkl")