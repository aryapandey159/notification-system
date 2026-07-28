# Notification System — Event-Driven Architecture with Apache Kafka

A production-grade, event-driven notification system built with Apache Kafka,Docker, Spring Boot, and MySQL. When an event is published (e.g. order placed), the system asynchronously processes it and sends an email notification to the user — with automatic retries and Dead Letter Queue support for failure handling.

---

## Architecture

```
┌─────────────────────┐        ┌─────────────────┐        ┌──────────────────────────────┐
│  notification-      │        │                 │        │  EmailNotification           │
│  service            │──────▶ │  Kafka Topic    │──────▶ │  MicroService                │
│  (Producer)         │        │  notification-  │        │  (Consumer)                  │
│                     │        │  event-topic    │        │                              │
│  POST /notifications│        │                 │        │  → Sends Email               │
│  GET  /notifications│        └─────────────────┘        │  → Saves to MySQL            │
│  GET  /notifications│                                    │  → Retries on failure        │
│        /{id}        │                                    │  → DLT after 3 retries       │
└─────────────────────┘                                    └──────────────────────────────┘
         │                                                              │
         └──────────────────── commonEntity ──────────────────────────┘
                               (Shared Models)
```

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot |
| Messaging | Apache Kafka |
| Database | MySQL |
| ORM | Spring Data JPA / Hibernate |
| Email | Spring Mail (Gmail SMTP) |
| Containerization | Docker |
| Build Tool | Maven |

---

## Modules

### 1. `notification-service` (Producer)
- Exposes REST APIs to create and fetch notifications
- Publishes `NotificationEvent` to Kafka topic `notification-event-topic`
- Persists notification records in MySQL

### 2. `EmailNotificationMicroService` (Consumer)
- Listens to `notification-event-topic`
- Sends email to user on successful event consumption
- Implements **exponential backoff retry** (1s → 2s → 4s)
- Moves failed messages to **Dead Letter Topic** (`notification-event-topic.DLT`) after 3 retries
- Logs all DLT events with topic, partition, offset, and error details

### 3. `commonEntity`
- Shared module containing `NotificationEvent` and common DTOs
- Used by both Producer and Consumer services

---

## Key Features

- **Async Processing** — Notifications sent asynchronously via Kafka, no blocking
- **Retry Mechanism** — Exponential backoff: retries 3 times before giving up
- **Dead Letter Queue** — Failed messages moved to DLT with full logging
- **Email Notifications** — Real emails sent via Gmail SMTP on success
- **REST APIs** — Full CRUD for notification management
- **Dockerized Kafka** — Entire Kafka setup runs via Docker Compose

---

## API Endpoints

### Notification Service

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/notifications` | Publish a new notification event |
| `GET` | `/notifications` | Get all notifications |
| `GET` | `/notifications/{id}` | Get notification by ID |

### Sample Request — POST `/notifications`

```json
{
  "userEmail": "user@example.com",
  "subject": "Order Placed",
  "message": "Your order #1234 has been placed successfully."
}
```

### Sample Response

```json
{
  "id": 1,
  "userEmail": "user@example.com",
  "subject": "Order Placed",
  "status": "SENT",
  "createdAt": "2026-07-27T10:00:00"
}
```

---

## How to Run Locally

### Prerequisites
- Java 17+
- Maven
- Docker & Docker Compose
- Gmail account with App Password enabled

### Step 1 — Clone the repository
```bash
git clone https://github.com/aryapandey159/notification-system.git
cd notification-system
```

### Step 2 — Create `.env` file in root
```
MYSQL_PASSWORD=yourpassword
MAIL_USERNAME=youremail@gmail.com
MAIL_PASSWORD=yourgmailapppassword
```

> **Gmail App Password:** Go to [myaccount.google.com/apppasswords](https://myaccount.google.com/apppasswords) to generate one. Do NOT use your real Gmail password.

### Step 3 — Start Kafka with Docker
```bash
docker-compose up -d
```

### Step 4 — Run notification-service (Producer)
```bash
cd notification-service
mvn spring-boot:run
```

### Step 5 — Run EmailNotificationMicroService (Consumer)
```bash
cd EmailNotificationMicroService
mvn spring-boot:run
```

### Step 6 — Test it
```bash
curl -X POST http://localhost:8080/notifications \
  -H "Content-Type: application/json" \
  -d '{"userEmail":"test@gmail.com","subject":"Test","message":"Hello!"}'
```

Check your email — you should receive a notification within seconds. ✅

---

## Error Handling Flow

```
Message arrives → Consumer processes → Fails
       ↓
Retry 1 (after 1s)  → Still fails
       ↓
Retry 2 (after 2s)  → Still fails
       ↓
Retry 3 (after 4s)  → Still fails
       ↓
Logs: "All retries exhausted. Sending to DLT"
       ↓
Message → notification-event-topic.DLT ✅
```

---

## Project Structure

```
notification-system/
├── commonEntity/                         # Shared models
│   └── src/main/java/com/commonEntity/
│       └── NotificationEvent.java
├── notification-service/                 # Producer service
│   └── src/main/java/
│       ├── Controller/
│       ├── Service/
│       ├── Entity/
│       ├── Repository/
│       └── DTO/
├── EmailNotificationMicroService/        # Consumer service
│   └── src/main/java/
│       └── Handler/
├── docker-compose.yml
└── README.md
```

---

## Environment Variables

| Variable | Description |
|---|---|
| `MYSQL_PASSWORD` | MySQL root password |
| `MAIL_USERNAME` | Gmail address for sending emails |
| `MAIL_PASSWORD` | Gmail App Password (not your real password) |

---

## Author

**Arya Kumar**
- GitHub: [@aryapandey159](https://github.com/aryapandey159)
- LinkedIn: [arya-kumar-415976202](https://linkedin.com/in/arya-kumar-415976202)
