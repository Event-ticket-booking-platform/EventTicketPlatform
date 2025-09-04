#!/bin/bash
set -e

echo "Applying ConfigMaps and Secrets..."
kubectl apply -f k8s/configmaps/common-configmap.yaml
kubectl apply -f k8s/configmaps/notificationservice-configmap.yaml
kubectl apply -f k8s/configmaps/ticketservice-configmap.yaml
kubectl apply -f k8s/configmaps/gateway-configmap.yaml
kubectl apply -f k8s/secrets/gateway-secret.yaml
kubectl apply -f k8s/secrets/ticketservice-secret.yaml

echo "Applying Services..."
kubectl apply -f k8s/services/notificationservice-service.yaml
kubectl apply -f k8s/services/ticketservice-service.yaml
kubectl apply -f k8s/services/ticketpostgres-service.yaml
kubectl apply -f k8s/services/kafka-service.yaml
kubectl apply -f k8s/services/zookeeper-service.yaml
kubectl apply -f k8s/services/gateway-service.yaml

echo "Deploying Databases..."
kubectl apply -f k8s/deployments/databases/ticketpostgres-deployment.yaml

echo "Deploying Kafka..."
kubectl apply -f k8s/deployments/infrastructure/

echo "Deploying Microservices..."
kubectl apply -f k8s/deployments/microservices/notificationservice-deployment.yaml
kubectl apply -f k8s/deployments/microservices/gateway-deployment.yaml
kubectl apply -f k8s/deployments/microservices/ticketservice-deployment.yaml

echo "Deployment complete! Check pods with: kubectl get pods"