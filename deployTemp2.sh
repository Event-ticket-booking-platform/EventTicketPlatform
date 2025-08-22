#!/bin/bash
set -e

echo "Applying ConfigMaps and Secrets..."
kubectl apply -f k8s/configmaps/common-configmap.yaml
kubectl apply -f k8s/configmaps/userservice-configmap.yaml
kubectl apply -f k8s/configmaps/eventservice-configmap.yaml
kubectl apply -f k8s/configmaps/notificationservice-configmap.yaml
kubectl apply -f k8s/secrets/userservice-secret.yaml
kubectl apply -f k8s/secrets/eventservice-secret.yaml
kubectl apply -f k8s/secrets/notificationservice-secret.yaml


echo "Applying Services..."
kubectl apply -f k8s/services/userservice-service.yaml
kubectl apply -f k8s/services/eventservice-service.yaml
kubectl apply -f k8s/services/notificationservice-service.yaml
kubectl apply -f k8s/services/mongo-service.yaml
kubectl apply -f k8s/services/postgres-service.yaml
kubectl apply -f k8s/services/kafka-service.yaml
kubectl apply -f k8s/services/zookeeper-service.yaml

echo "Deploying Databases..."
kubectl apply -f k8s/deployments/databases/mongo-deployment.yaml
kubectl apply -f k8s/deployments/databases/postgres-deployment.yaml

echo "Deploying Kafka..."
kubectl apply -f k8s/deployments/infrastructure/

echo "Deploying Microservices..."
kubectl apply -f k8s/deployments/microservices/userservice-deployment.yaml
kubectl apply -f k8s/deployments/microservices/eventservice-deployment.yaml
kubectl apply -f k8s/deployments/microservices/notificationservice-deployment.yaml

echo "Deployment complete! Check pods with: kubectl get pods"
