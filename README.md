# Home Energy Management System (HEMS)

A centralized, event-driven microservices platform for monitoring real-time electricity consumption across household appliances, detecting abnormal power spikes, and delivering actionable insights and alerts.

---

## Overview

HEMS ingests high-velocity telemetry from smart meters and IoT devices, streams it asynchronously through Kafka, persists it to a time-series store, and surfaces it through secured REST APIs. The system is built to be production-ready — containerized, observable, and fault-tolerant — with a setup time of under 5 minutes.

**Highlights**
- Real-time monitoring across 10+ household appliances
- Automated abnormal power-spike detection and alerting
- 7-service microservices backend
- 5+ secured REST APIs exposed via an API Gateway
- Automated database migrations and end-to-end observability (service health, latency, resilience)

---

## Architecture


```
                  
                                      [ CLIENT ]               ( IoT )
                                        │                        │
                                        ▼                        ▼
                               ┌──────────────────────────────────┐
                               │           API Gateway            │────┐
                               └──────────────────────────────────┘    │
                                 │          │           │              │
                                 ▼          ▼           ▼              ▼
                         ┌───────────┐┌───────────┐┌───────────┐ ┌─────────────┐
                         │  Device   ││   User    ││ Ingestion │ │   Insight   │
                         │  Service  ││  Service  ││  Service  │ │   Service   │
                         └─────┬─────┘└─────┬─────┘└─────┬─────┘ └──────┬──────┘
                               │            │            │              │
                               │            │            ▼              ▼
                               │            │        ( Kafka )       [  AI  ]
                               │            │            │
                               ▼            │            ▼
                           ( MySQL )        │      ┌───────────┐
                               ▲            │      │   Usage   │
                               │            │      │  Service  │
                               │            │      └─────┬─────┘
                               │            │      ┌─────┴──────┬──────────────┐
                               │            ▼      ▼            ▼              ▼
                               │        (  MySQL  )       (Timeseries)     ( Kafka )
                               │                                │              │
                               │  - - - - - - - - - - - - - - - ┘              ▼
                               │ ┌ - - - - - - - - - - - - - - - - - - - ┌───────────┐
                               │ │                                       │   Alert   │
                               └─┴ - - - - - - - - - - - - - - - - - - - │  Service  │
                                                                         └───────────┘

```
### Core Flow
1. **Registration & Auth** — Users and households onboard through the *User Service*; devices are registered via the *Device Service*.
2. **Ingestion** — Smart meters push telemetry to the *Ingestion Service*, which forwards it to Kafka for non-blocking, asynchronous processing.
3. **Processing** — The *Usage Service* consumes telemetry from Kafka, aggregates it against user/device metadata, and writes it to the time-series store.
4. **Alerting** — Usage events are streamed to the *Alert Service*, which evaluates them against configured thresholds and dispatches notifications for abnormal spikes.
5. **Insights** — The *Insight Service* analyzes historical usage patterns and returns AI-generated recommendations for optimizing consumption.

---

## Microservices

| Service | Responsibility |
|---|---|
| **API Gateway** | Single entry point; routing, authentication, and circuit breaking |
| **User Service** | Manages user accounts, households, and profile data |
| **Device Service** | Registers and manages metadata for smart meters/IoT devices |
| **Ingestion Service** | High-throughput endpoint for receiving streaming telemetry |
| **Usage Service** | Consumes Kafka streams, aggregates metrics, writes to time-series DB |
| **Alert Service** | Monitors usage against rules, triggers spike/anomaly notifications |
| **Insight Service** | Generates plain-language energy-saving recommendations |

---

## Tech Stack

**Backend:** Java, Spring Boot, Spring Data JPA
**Messaging:** Apache Kafka
**Databases:** MySQL (relational), InfluxDB (time-series)
**Identity & Access:** Keycloak (OAuth2 / JWT-based authentication)
**Infrastructure:** Docker (containerization), automated DB migrations
**Observability:** Health checks, latency and resilience monitoring
**API Docs:** Swagger / OpenAPI

---

## Features

- 🔌 Real-time consumption tracking across multiple household appliances
- ⚠️ Automated anomaly detection with configurable alert thresholds
- 📈 Time-series storage optimized for dense energy usage data
- 🔐 Secure, authenticated REST APIs behind an API Gateway
- 🐳 Fully containerized for fast, repeatable deployment
- 🩺 Built-in observability for service health, latency, and fault tolerance

---

## Getting Started

### Prerequisites
- Java 17+
- Docker & Docker Compose
- Maven

### Setup
```bash
# Clone the repository
git clone <repo-url>
cd hems

# Start infrastructure (Kafka, MySQL, InfluxDB, Keycloak)
docker-compose up -d

# Build and run services
mvn clean install
docker-compose up --build
```

### API Documentation
Once running, Swagger UI is available per service at:
```
http://localhost:<service-port>/swagger-ui.html
```
