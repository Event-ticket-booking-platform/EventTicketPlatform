#!/bin/bash
set -e

echo "Applying ConfigMaps and Secrets..."
kubectl apply -f k8s/configmaps/common-configmap.yaml
# kubectl apply -f k8s/configmaps/orderservice-configmap.yaml
kubectl apply -f k8s/configmaps/ticketservice-configmap.yaml
# kubectl apply -f k8s/configmaps/paymentservice-configmap.yaml
# kubectl apply -f k8s/secrets/orderservice-secret.yaml
kubectl apply -f k8s/secrets/ticketservice-secret.yaml
# kubectl apply -f k8s/secrets/paymentservice-secret.yaml

echo "Applying Services..."
# kubectl apply -f k8s/services/orderservice-service.yaml
kubectl apply -f k8s/services/ticketservice-service.yaml
# kubectl apply -f k8s/services/paymentservice-service.yaml
kubectl apply -f k8s/services/kafka-service.yaml
kubectl apply -f k8s/services/zookeeper-service.yaml
# kubectl apply -f k8s/services/orderpostgres-service.yaml
kubectl apply -f k8s/services/ticketpostgres-service.yaml
# kubectl apply -f k8s/services/paymentpostgres-service.yaml

echo "Deploying Databases..."
# kubectl apply -f k8s/deployments/databases/orderpostgres-deployment.yaml
kubectl apply -f k8s/deployments/databases/ticketpostgres-deployment.yaml
# kubectl apply -f k8s/deployments/databases/paymentpostgres-deployment.yaml

echo "Deploying Kafka..."
kubectl apply -f k8s/deployments/infrastructure/

echo "Deploying Microservices..."
# kubectl apply -f k8s/deployments/microservices/orderservice-deployment.yaml
kubectl apply -f k8s/deployments/microservices/ticketservice-deployment.yaml
# kubectl apply -f k8s/deployments/microservices/paymentservice-deployment.yaml

echo "Deployment complete! Check pods with: kubectl get pods"
