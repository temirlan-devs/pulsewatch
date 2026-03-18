# PulseWatch

**PulseWatch** is a backend platform for **real-time system monitoring, anomaly detection, and operational insights**.

It ingests service metrics and logs, evaluates service health, detects anomalies, generates alerts, and produces risk-based insights about system behavior.

The project demonstrates how a production-style monitoring backend can be designed using **clean architecture, structured APIs, and explainable intelligence layers**.

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

- **OK** — service operating normally  
- **WARN** — abnormal behavior detected  
- **ERROR** — failure conditions detected  
- **NO_DATA** — monitoring telemetry missing  

---

## Anomaly Detection
PulseWatch continuously compares recent metrics against historical baselines.

The system detects anomalies such as:

- sudden error rate spikes  
- abnormal latency increases  
- unusual request patterns  

---

## Alert Management System
PulseWatch includes a full alert lifecycle:

Alert states:
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

This layer provides early signals of potential system instability.

---

## AI-style Operational Insights 
PulseWatch generates structured explanations for service behavior.

Example insight output:

```text
Service: payment-service

Summary:
Service latency has increased significantly during the last monitoring window.

Likely Cause:
Average request latency exceeded normal operational thresholds.

Recommended Action:
Inspect downstream dependencies and recent deployment changes.
```

---

## Architecture

PulseWatch follows a layered backend architecture:

```text
Controller Layer → Service Layer → Repository Layer → Database
```

Core components include:

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
Retrieve metrics for a service and time range:

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
Retrieve the current health status of a service:

```http
GET /services/{service}/health
```

---

### Service Insights
Retrieve operational explanation and recommended actions:

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

## Technology Stack

- Java  
- Spring Boot  
- Spring Data JPA  
- PostgreSQL  
- REST APIs  

---

## Project Goals

PulseWatch demonstrates:

- backend system design  
- monitoring architecture  
- anomaly detection logic  
- alert lifecycle management  
- operational insights generation  

---

## Future Improvements

Possible extensions:

- real ML anomaly detection  
- streaming ingestion  
- distributed metrics pipeline  
- dashboard visualization  
- LLM-generated operational summaries  

---

## System Architecture

![PulseWatch Architecture](docs/architecture.svg)

---

## How to Run

### Prerequisites

- Java 17+  
- PostgreSQL  

### Setup

1. Clone the repository:

```bash
git clone https://github.com/your-username/pulsewatch.git
cd pulsewatch/backend
```

2. Configure database in `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/pulsewatch
spring.datasource.username=your_username
spring.datasource.password=your_password
```

3. Run the application:

```bash
./mvnw spring-boot:run
```

---

### API Base URL

```text
http://localhost:8080
```

---

## API Examples

### Create Metric

```http
POST /metrics
Content-Type: application/json
```

```json
{
  "service": "auth-service",
  "requestCount": 120,
  "errorCount": 5,
  "averageLatency": 180.5,
  "timestamp": 1772911425000
}
```

---

### Get Metrics

```http
GET /metrics?service=auth-service&from=1772911000000&to=1772912000000
```

---

### Metrics Summary

```http
GET /metrics/summary?service=auth-service&from=1772911000000&to=1772912000000
```

```json
{
  "service": "auth-service",
  "totalRequests": 12000,
  "totalErrors": 220,
  "errorRate": 0.018,
  "averageLatency": 145.3
}
```

---

### Service Health

```http
GET /services/auth-service/health
```

```json
{
  "service": "auth-service",
  "status": "WARN",
  "errorRate": 0.06,
  "averageLatency": 320.0,
  "lastMetricTimestamp": 1772911425000,
  "lastLogTimestamp": 1772911423642
}
```

---

### Service Insights

```http
GET /services/auth-service/insights
```

```json
{
  "service": "auth-service",
  "summary": "Elevated error rate detected.",
  "likelyCause": "Spike in failed authentication requests.",
  "recommendedAction": "Check authentication service dependencies and recent deployments."
}
```

---

### Alerts

```http
GET /alerts?service=auth-service&status=OPEN
```

```json
[
  {
    "service": "auth-service",
    "type": "ANOMALY",
    "status": "OPEN",
    "severity": "HIGH",
    "message": "Error rate exceeded threshold",
    "timestamp": 1772911425000
  }
]
```
