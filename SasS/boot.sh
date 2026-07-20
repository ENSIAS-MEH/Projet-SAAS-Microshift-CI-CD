#!/bin/bash
# deploy-tenant.sh
# Deploiement d'un environnement isole (own namespace, own MySQL, own app pod)
# on MicroShift (solution moins couteux au niveau de resources) pour ce projet
#
# Usage:
#   ./deploy-tenant.sh <tenant-name>
#
# Example:
#   ./deploy-tenant.sh acme
# 

set -e

if [ -z "$1" ]; then
  echo "Usage: $0 <tenant-name>"
  echo "Example: $0 acme"
  exit 1
fi

TENANT=$1
NAMESPACE="tenant-$TENANT"
DB_PASSWORD="StrongAdminPassword"
DB_NAME="projetweb_$TENANT"
APP_IMAGE="ennajahmalek/publish-build:57"

echo "=== Deploying tenant '$TENANT' in namespace '$NAMESPACE' ==="

# 1. Create namespace (skip if it already exists)
if oc get namespace "$NAMESPACE" >/dev/null 2>&1; then
  echo "Namespace $NAMESPACE already exists, reusing it."
else
  oc create namespace "$NAMESPACE"
fi
 
oc adm policy add-scc-to-user anyuid -z default -n "$NAMESPACE" >/dev/null 2>&1 || true
 
cat <<EOF | oc apply -f -
apiVersion: apps/v1
kind: Deployment
metadata:
  name: factory-db
  namespace: $NAMESPACE
spec:
  replicas: 1
  selector:
    matchLabels:
      app: factory-db
  template:
    metadata:
      labels:
        app: factory-db
    spec:
      securityContext:
        runAsNonRoot: true
        seccompProfile:
          type: RuntimeDefault
      containers:
      - name: mysql
        image: mysql:8.0
        securityContext:
          allowPrivilegeEscalation: false
          capabilities:
            drop: ["ALL"]
          runAsNonRoot: true
        env:
        - name: MYSQL_ROOT_PASSWORD
          value: "$DB_PASSWORD"
        - name: MYSQL_DATABASE
          value: "$DB_NAME"
        ports:
        - containerPort: 3306
        volumeMounts:
        - name: mysql-storage
          mountPath: /var/lib/mysql
      volumes:
      - name: mysql-storage
        emptyDir: {}
---
apiVersion: v1
kind: Service
metadata:
  name: db
  namespace: $NAMESPACE
spec:
  selector:
    app: factory-db
  ports:
  - port: 3306
    targetPort: 3306
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: factory-saas
  namespace: $NAMESPACE
spec:
  replicas: 1
  selector:
    matchLabels:
      app: factory-saas
  template:
    metadata:
      labels:
        app: factory-saas
    spec:
      securityContext:
        runAsNonRoot: true
        seccompProfile:
          type: RuntimeDefault
      containers:
      - name: publish-build
        image: $APP_IMAGE
        securityContext:
          allowPrivilegeEscalation: false
          capabilities:
            drop: ["ALL"]
          runAsNonRoot: true
        env:
        - name: SPRING_DATASOURCE_URL
          value: "jdbc:mysql://db:3306/$DB_NAME"
        - name: SPRING_DATASOURCE_USERNAME
          value: "root"
        - name: SPRING_DATASOURCE_PASSWORD
          value: "$DB_PASSWORD"
        ports:
        - containerPort: 8181
---
apiVersion: v1
kind: Service
metadata:
  name: factory-saas
  namespace: $NAMESPACE
spec:
  selector:
    app: factory-saas
  ports:
  - port: 8181
    targetPort: 8181
---
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: deny-cross-namespace
  namespace: $NAMESPACE
spec:
  podSelector: {}
  policyTypes:
  - Ingress
  ingress:
  - from:
    - podSelector: {}
---
apiVersion: v1
kind: ResourceQuota
metadata:
  name: tenant-quota
  namespace: $NAMESPACE
spec:
  hard:
    requests.cpu: "500m"
    requests.memory: 512Mi
    limits.cpu: "1"
    limits.memory: 1Gi
EOF

echo "" 
sleep 5
oc get pods -n "$NAMESPACE"

echo ""
echo "=== Done ==="
echo "Equipe:    $TENANT"
echo "Namespace: $NAMESPACE"
echo "DB name:   $DB_NAME"
echo ""
echo "To check status:"
echo "  oc get pods -n $NAMESPACE"

# cherche d'un port libre
FREE_PORT=$(python3 -c 'import socket; s=socket.socket(); s.bind(("",0)); print(s.getsockname()[1]); s.close()')

echo "" 
oc port-forward -n "$NAMESPACE" svc/factory-saas "$FREE_PORT":8181 > /tmp/portforward-$TENANT.log 2>&1 &
PF_PID=$!
 
echo ""
echo ">>> Open: http://localhost:$FREE_PORT"
echo ""  
