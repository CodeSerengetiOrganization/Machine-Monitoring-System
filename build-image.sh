#!/bin/bash
set -e

IMAGE_NAME="mms-backend:1.0.0"
IMAGE_TAR="mms-backend.tar"

echo "=== Step 1: Build Spring Boot JAR ==="
mvn clean package -DskipTests

echo "=== Step 2: Build Docker image ==="
docker build -t $IMAGE_NAME .

echo "=== Step 3: Save Docker image to TAR file ==="
docker save -o $IMAGE_TAR $IMAGE_NAME

echo "=== Step 4: Copy TAR file to WSL2 ==="
WSL_PATH="/mnt/c/Users/$USER/Downloads/$IMAGE_TAR"
cp $IMAGE_TAR $WSL_PATH

echo "=== DONE ==="
echo "Image exported to: $WSL_PATH"
echo "Next: move to WSL2 and import image into k3s."
