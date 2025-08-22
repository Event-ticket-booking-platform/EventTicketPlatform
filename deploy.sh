#!/bin/bash
set -e

echo "Applying ConfigMaps and Secrets..."
kubectl apply -f k8s/configmaps/
kubectl apply -f k8s/secrets/

echo "Applying Services..."
kubectl apply -f k8s/services/

echo "Deploying Databases..."
kubectl apply -f k8s/deployments/databases/

echo "Deploying Kafka..."
kubectl apply -f k8s/deployments/infrastructure/

echo "Deploying Microservices..."
kubectl apply -f k8s/deployments/microservices/

echo "Deployment complete! Check pods with: kubectl get pods"
