#!/bin/bash
set -e

echo "Deleting Microservices..."
kubectl delete -f k8s/deployments/microservices/ || true
kubectl delete -f k8s/services/ || true

echo "Deleting Kafka..."
kubectl delete -f k8s/deployments/infrastructure/ || true

echo "Deleting Databases..."
kubectl delete -f k8s/deployments/databases/ || true

echo "Deleting ConfigMaps and Secrets..."
kubectl delete -f k8s/configmaps/ || true
kubectl delete -f k8s/secrets/ || true

echo "Cleanup complete! Check remaining resources with: kubectl get all"
