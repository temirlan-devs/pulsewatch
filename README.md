# PulseWatch

**PulseWatch** is a backend platform for **real-time system monitoring, anomaly detection, and operational insights**.

It ingests service metrics and logs, evaluates service health, detects anomalies, manages alerts, and generates **risk-based predictions and explainable insights** about system behavior.

The project demonstrates how a **production-grade monitoring backend** can be designed using clean architecture, structured APIs, and layered intelligence components.

---

## Key Features

### Real-time Monitoring
PulseWatch ingests and stores operational metrics and logs from multiple services.

Example metrics:
- request count  
- error count  
- latency  

---

### Service Health Evaluation
Each service health status is computed using monitoring signals.

Possible states:
- **OK** — service operating normally  
- **WARN** — abnormal behavior detected  
- **ERROR** — failure conditions detected  
- **NO_DATA** — monitoring telemetry missing  

---

### Anomaly Detection
PulseWatch compares recent metrics against historical baselines using a **window-based analysis approach**.

Detects:
- sudden error rate spikes  
- abnormal latency increases  
- unusual traffic patterns  

---

### Alert Management System
Full alert lifecycle with state transitions:

- **OPEN**
- **ACKNOWLEDGED**
- **RESOLVED**

Features:
- severity levels  
- cooldown windows  
- deduplication logic  
- service-level filtering  

---

### Prediction & Risk Scoring
PulseWatch computes **risk scores and predicted service states** based on:

- anomaly signals  
- service health  
- active alerts  
- metric trends  

Outputs:
- predicted service status  
- risk score (0–1)  
- confidence level  
- reasoning signals  

---

### Insights Engine (Explainable Intelligence)
Generates structured explanations by combining:

- metric analysis  
- alert state  
- log signals  

Example output:

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

### Asynchronous Metric Ingestion
Metrics are processed via an **internal queue with retry handling**, enabling:

- decoupled ingestion  
- improved reliability  
- backpressure handling  

---

### Configurable Monitoring Behavior
System thresholds and analysis windows are configurable via `application.properties`, including:

- freshness thresholds
- alert cooldown windows
- log analysis windows
- log analysis limits
- ingestion limits

---

### Interactive API Documentation (Swagger)
PulseWatch provides interactive API documentation via Swagger (OpenAPI).

Access after starting the app:

```text
http://localhost:8080/swagger-ui/index.html
```

You can:
- explore endpoints  
- execute requests  
- inspect request/response formats  

---

### Test Coverage (Core Intelligence Layer)
Unit tests cover key system logic:

- metric window analysis  
- service prediction engine  
- insights generation  

---

## Architecture

![PulseWatch Architecture](docs/architecture.svg)

PulseWatch follows a layered architecture:
```text
Controller Layer → Service Layer → Repository Layer → Database
```

Core components:
- metric ingestion  
- log ingestion  
- health evaluation  
- anomaly detection  
- alert system  
- prediction engine  
- insights engine  
- system overview  

---

## Data Flow

1. Services send metrics and logs
2. Metrics are queued and processed asynchronously
3. Data is stored in PostgreSQL
4. The system evaluates:
    - service health
    - anomaly detection
    - alert generation
5. The prediction engine calculates:
    - risk score
    - predicted status
6. The insights engine generates:
    - human-readable explanations
7. Results are exposed through REST APIs and Swagger UI

---

## API Endpoints Overview

### Health
- `GET /health`

### Metrics
- `POST /metrics` (ingest metric)
- `GET /metrics` (paged fetch; query params: service, from, to, page, size, sort)
- `GET /metrics/summary?service=...&from=...&to=...`
- `GET /metrics/services`

### Logs
- `POST /logs` (ingest log)
- `GET /logs` (paged fetch; query params: service, from, to, page, size, sort)

### Service Health
- `GET /services/{service}/health`
- `GET /services/health`

### Service Insights
- `GET /services/{service}/insights`

### Service Prediction
- `GET /services/{service}/prediction`
- `GET /services/predictions`
- `GET /services/risk-ranking`

### System Overview
- `GET /system/overview`

### Alert Management
- `GET /alerts` (paged fetch; query params: service, type, status, severity, from, to, page, size, sort)
- `GET /alerts/stats`
- `POST /alerts/{id}/acknowledge`
- `POST /alerts/{id}/resolve`

---

## Components
- Ingestion layer: `MetricIngestionController`, `LogIngestionController`, `MetricQueue` and queue retry handling
- Health and anomaly engine: `ServiceHealthController`, `ServiceHealthService`, `MetricEntryService`
- Prediction engine: `ServicePredictionController`, `ServicePredictionService`
- Insights engine: `ServiceInsightsController`, `ServiceInsightsService`
- Alert lifecycle: `AlertController`, `AlertService`, `AlertRepository`
- System overview: `SystemOverviewController`, `SystemOverviewService`
- Persistence layer: JPA repositories under `repository`, DTOs under `dto`
- API docs: Swagger UI at `/swagger-ui/index.html`

## Technology Stack

- Java  
- Spring Boot  
- Spring Data JPA  
- PostgreSQL  
- REST APIs  
- Maven  
- Swagger (OpenAPI)  

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

## API Base URL

```text
http://localhost:8080
```

---

## Example Requests

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

```http
GET /alerts/stats
```

```http
POST /alerts/{id}/acknowledge
```

```http
POST /alerts/{id}/resolve
```

```json
{
  "service": "auth-service",
  "type": "ANOMALY",
  "status": "OPEN",
  "severity": "HIGH",
  "message": "Error rate exceeded threshold",
  "timestamp": 1772911425000
}
```

---

### Logs

```http
POST /logs
Content-Type: application/json
```

```json
{
  "level": "ERROR",
  "message": "Database connection timeout",
  "service": "auth-service",
  "timestamp": 1772911425000
}
```

```http
GET /logs?service=auth-service&from=1772911000000&to=1772912000000
```

---

### Service Health (all)

```http
GET /services/health
```

---

### Service Prediction

```http
GET /services/auth-service/prediction
```

```http
GET /services/predictions
```

```http
GET /services/risk-ranking
```

---

### System Overview

```http
GET /system/overview
```
