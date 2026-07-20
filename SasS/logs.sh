#!/bin/bash
# deploy-logging.sh
# Deploys Grafana + Loki + Promtail for cluster-wide log aggregation on MicroShift.
#
# Usage:
#   ./deploy-logging.sh
#
# Requires: KUBECONFIG already exported and pointing at your CRC/MicroShift cluster.

set -e

NAMESPACE="logging"

echo "=== Deploying logging stack (Grafana + Loki + Promtail) in namespace '$NAMESPACE' ==="

# 1. Namespace + SCC
if oc get namespace "$NAMESPACE" >/dev/null 2>&1; then
  echo "Namespace $NAMESPACE already exists, reusing it."
else
  oc create namespace "$NAMESPACE"
fi

oc adm policy add-scc-to-user anyuid -z default -n "$NAMESPACE" >/dev/null 2>&1 || true
oc adm policy add-scc-to-user anyuid -z promtail -n "$NAMESPACE" >/dev/null 2>&1 || true

# Promtail needs to read logs from all namespaces, so it needs a cluster-wide
# service account with permission to read pod logs/metadata.
oc create serviceaccount promtail -n "$NAMESPACE" 2>/dev/null || echo "Service account promtail already exists, reusing it."

cat <<EOF | oc apply -f -
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRole
metadata:
  name: promtail-reader
rules:
- apiGroups: [""]
  resources: ["pods", "nodes", "namespaces"]
  verbs: ["get", "watch", "list"]
---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: promtail-reader-binding
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: promtail-reader
subjects:
- kind: ServiceAccount
  name: promtail
  namespace: $NAMESPACE
EOF

# 2. Loki (log store)
cat <<EOF | oc apply -f -
apiVersion: v1
kind: ConfigMap
metadata:
  name: loki-config
  namespace: $NAMESPACE
data:
  loki-config.yaml: |
    auth_enabled: false
    server:
      http_listen_port: 3100
    common:
      path_prefix: /loki
      storage:
        filesystem:
          chunks_directory: /loki/chunks
          rules_directory: /loki/rules
      replication_factor: 1
      ring:
        instance_addr: 127.0.0.1
        kvstore:
          store: inmemory
    schema_config:
      configs:
      - from: 2020-10-24
        store: tsdb
        object_store: filesystem
        schema: v13
        index:
          prefix: index_
          period: 24h
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: loki
  namespace: $NAMESPACE
spec:
  replicas: 1
  selector:
    matchLabels:
      app: loki
  template:
    metadata:
      labels:
        app: loki
    spec:
      securityContext:
        runAsNonRoot: true
        seccompProfile:
          type: RuntimeDefault
      containers:
      - name: loki
        image: grafana/loki:2.9.4
        args:
        - -config.file=/etc/loki/loki-config.yaml
        securityContext:
          allowPrivilegeEscalation: false
          capabilities:
            drop: ["ALL"]
          runAsNonRoot: true
        ports:
        - containerPort: 3100
        volumeMounts:
        - name: loki-config
          mountPath: /etc/loki
        - name: loki-storage
          mountPath: /loki
      volumes:
      - name: loki-config
        configMap:
          name: loki-config
      - name: loki-storage
        emptyDir: {}
---
apiVersion: v1
kind: Service
metadata:
  name: loki
  namespace: $NAMESPACE
spec:
  selector:
    app: loki
  ports:
  - port: 3100
    targetPort: 3100
EOF

# 3. Promtail (log shipper) - DaemonSet so it runs on every node and reads container logs
cat <<EOF | oc apply -f -
apiVersion: v1
kind: ConfigMap
metadata:
  name: promtail-config
  namespace: $NAMESPACE
data:
  promtail-config.yaml: |
    server:
      http_listen_port: 9080
    positions:
      filename: /tmp/positions.yaml
    clients:
    - url: http://loki:3100/loki/api/v1/push
    scrape_configs:
    - job_name: kubernetes-pods
      kubernetes_sd_configs:
      - role: pod
      pipeline_stages:
      - cri: {}
      relabel_configs:
      - source_labels: [__meta_kubernetes_pod_node_name]
        target_label: node
      - source_labels: [__meta_kubernetes_namespace]
        target_label: namespace
      - source_labels: [__meta_kubernetes_pod_name]
        target_label: pod
      - source_labels: [__meta_kubernetes_pod_container_name]
        target_label: container
      - source_labels: [__meta_kubernetes_pod_uid]
        target_label: __path__
        replacement: /var/log/pods/*\$1/*/*.log
---
apiVersion: apps/v1
kind: DaemonSet
metadata:
  name: promtail
  namespace: $NAMESPACE
spec:
  selector:
    matchLabels:
      app: promtail
  template:
    metadata:
      labels:
        app: promtail
    spec:
      serviceAccountName: promtail
      containers:
      - name: promtail
        image: grafana/promtail:2.9.4
        args:
        - -config.file=/etc/promtail/promtail-config.yaml
        env:
        - name: HOSTNAME
          valueFrom:
            fieldRef:
              fieldPath: spec.nodeName
        securityContext:
          privileged: true
          runAsUser: 0
        volumeMounts:
        - name: promtail-config
          mountPath: /etc/promtail
        - name: varlog
          mountPath: /var/log
          readOnly: true
        - name: varlibdockercontainers
          mountPath: /var/lib/containers
          readOnly: true
      volumes:
      - name: promtail-config
        configMap:
          name: promtail-config
      - name: varlog
        hostPath:
          path: /var/log
      - name: varlibdockercontainers
        hostPath:
          path: /var/lib/containers
EOF

# 4. Grafana, pre-wired with Loki as a datasource + a pre-built logs table dashboard
#
# NOTE: the dashboard JSON is merged directly into grafana-dashboard-provider
# (same ConfigMap that's mounted as a whole directory at
# /etc/grafana/provisioning/dashboards). Do NOT reintroduce a separate
# ConfigMap mounted via subPath into that same directory - that caused
# "mount ... Not a directory" CreateContainerError previously.
cat <<EOF | oc apply -f -
apiVersion: v1
kind: ConfigMap
metadata:
  name: grafana-datasources
  namespace: $NAMESPACE
data:
  datasources.yaml: |
    apiVersion: 1
    datasources:
    - name: Loki
      type: loki
      access: proxy
      url: http://loki:3100
      isDefault: true
---
apiVersion: v1
kind: ConfigMap
metadata:
  name: grafana-dashboard-provider
  namespace: $NAMESPACE
data:
  dashboards.yaml: |
    apiVersion: 1
    providers:
    - name: default
      orgId: 1
      folder: ""
      type: file
      disableDeletion: false
      updateIntervalSeconds: 30
      options:
        path: /etc/grafana/provisioning/dashboards
  logs-table.json: |
    {
      "title": "All Logs (Table)",
      "uid": "all-logs-table",
      "timezone": "browser",
      "time": { "from": "now-1h", "to": "now" },
      "panels": [
        {
          "id": 1,
          "type": "table",
          "title": "Logs",
          "gridPos": { "h": 20, "w": 24, "x": 0, "y": 0 },
          "datasource": { "type": "loki", "uid": "Loki" },
          "targets": [
            {
              "expr": "{namespace=~\".+\"}",
              "queryType": "range",
              "refId": "A"
            }
          ],
          "transformations": [
            {
              "id": "organize",
              "options": {
                "excludeByName": { "id": true, "tsNs": true },
                "indexByName": {
                  "Time": 0,
                  "namespace": 1,
                  "pod": 2,
                  "container": 3,
                  "Line": 4
                },
                "renameByName": {
                  "Time": "Time",
                  "namespace": "Namespace",
                  "pod": "Pod",
                  "container": "Container",
                  "Line": "Message"
                }
              }
            }
          ],
          "options": {
            "showHeader": true,
            "sortBy": [{ "displayName": "Time", "desc": true }]
          }
        }
      ],
      "schemaVersion": 39,
      "version": 1
    }
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: grafana
  namespace: $NAMESPACE
spec:
  replicas: 1
  selector:
    matchLabels:
      app: grafana
  template:
    metadata:
      labels:
        app: grafana
    spec:
      securityContext:
        runAsNonRoot: true
        seccompProfile:
          type: RuntimeDefault
      containers:
      - name: grafana
        image: grafana/grafana:11.2.0
        securityContext:
          allowPrivilegeEscalation: false
          capabilities:
            drop: ["ALL"]
          runAsNonRoot: true
        env:
        - name: GF_SECURITY_ADMIN_USER
          value: "admin"
        - name: GF_SECURITY_ADMIN_PASSWORD
          value: "admin"
        ports:
        - containerPort: 3000
        volumeMounts:
        - name: grafana-datasources
          mountPath: /etc/grafana/provisioning/datasources
        - name: grafana-dashboard-provider
          mountPath: /etc/grafana/provisioning/dashboards
      volumes:
      - name: grafana-datasources
        configMap:
          name: grafana-datasources
      - name: grafana-dashboard-provider
        configMap:
          name: grafana-dashboard-provider
---
apiVersion: v1
kind: Service
metadata:
  name: grafana
  namespace: $NAMESPACE
spec:
  selector:
    app: grafana
  ports:
  - port: 3000
    targetPort: 3000
EOF

echo ""
echo "=== Waiting for pods to come up (this can take 30-60s) ==="
sleep 10
oc get pods -n "$NAMESPACE"

# 5. Auto port-forward Grafana to a free local port
FREE_PORT=$(python3 -c 'import socket; s=socket.socket(); s.bind(("",0)); print(s.getsockname()[1]); s.close()')

echo ""
echo "=== Starting port-forward for Grafana on free local port $FREE_PORT ==="
oc port-forward -n "$NAMESPACE" svc/grafana "$FREE_PORT":3000 > /tmp/portforward-grafana.log 2>&1 &
PF_PID=$!

echo ""
echo "=== Done ==="
echo "Login:    admin / admin"
echo ">>> Open: http://localhost:$FREE_PORT"
echo ""
echo "Loki is pre-wired as the default datasource."
echo "In Grafana, go to Explore, pick Loki, and query e.g.:"
echo '  {namespace="tenant-malek"}'
echo ""
echo "To stop the port-forward later:"
echo "  kill $PF_PID"