# PulseWatch

**PulseWatch** is a backend platform for **real-time system monitoring, anomaly detection, and operational insights**.

It insights service metrics and logs, evaluates service health, detects anomalies, generates alerts, and produces risk-based insights about system behavior.

The project demonstrates how a production-style monitoring backend can be designed using **clear architecture, structured APIs, and explainable intelligence layers**.

---

# Key Features

## Real-time Monitoring
PulseWatch ingests and stores operational metrics and logs from multiple services.

Example metrics:
- request count
- error count
- latency

---

## Service Health Evaluation
Each service health status is computed using monitoring signals.

Possible states:

- **OK** - service operating normally
- **WARN** - abnormal behavior detected
- **ERROR** - failure conditions detected
- **NO_DATA** - monitoring telemtry missing

---

## Anomaly Detection
PulseWatch continuously compares recent metrics against historical baselines.

The system detects anomalies such as:

- sudden error rate spikes
- abnormal latency increases
- unusual request patters

---

## Alert Management System
PulseWatch includes a full alert lifecycle:

Alert States:
- **OPEN**
- **ACKNOWLEDGED**
- **RESOLVED**

Features include:
- alert severity levels
- cooldown windows
- deduplication logic
- service-level filtering

---

## Prediction & Risk Scoring
PulseWatch computes operational risk scores based on service behavior.

Predictions include:

- predicted service status
- risk score estimation
- anomaly indicators

This layer provides early signals of potential system instability

---

## AI-style Operational Insights 
PulseWatch generates structured explanations for service behavior.

Example insight output:

'''text 
Service: payment-service

Summary:
Service latency has inreased significantly during the last monitoring window.

Likely Cause:
Average request latency exceeded normal operational thresholds.

Recommended Action:
Inspect downstream dependencies and recent deployment changes.
```

--- 

## Architecture
PulseWatch follows a layered backend architecture:

```text
Controlled Layer -> Service Layer -> Repository Layer -> Database
```

Core componets include:

- Metric ingestion
- Log ingestion
- Health evaluation
- Anomaly detection
- Alert system
- Prediction engine
- Insights engine
- System overview

---

## Example API Endpoints

### Metrics
Retrieve metrics for a service and time range

```http
GET /metrics?service=auth-service&from=...&to=...
```

---

### Metrics Summary
Retrieve aggregated metrics statistics:

```http
GET /metrics/summary?service=auth-service&from=...&to=...
```

---

### Service Health
Retrieve the current health status of a service.

```http
GET /services/{services}/health
```

---

### Service Insights

Retrieve operational explanation and recommended action.

```http
GET /services/{service}/insights
```

---

### Alerts

Retrieve alerts:

```http
GET /alerts
```

Filter alerts:

```http
GET /alerts?service=auth-service
GET /alerts?severity=CRITICAL
GET /alerts?status=OPEN
```

---
