# FinFlow 🏦

A production-inspired monolithic banking backend built with Java and Spring Boot, designed to simulate real-world fintech engineering challenges.

---

## 🚀 Features

| Feature | Tech Used |
|---|---|
| Money Transfers | Spring JPA, PostgreSQL |
| Fraud Detection | Kafka, CompletableFuture, @Async |
| Rate Limiting | Bucket4j + Redis |
| Distributed Locking | Redisson RLock |
| Idempotency | Redis (24hr TTL) |
| Circuit Breaker | Resilience4j |
| JWT Authentication | Spring Security, JJWT |
| Caching | Redis, @Cacheable |
| Batch Processing | @Async, Thread Pool |
| Pagination | Spring Pageable |

---

## 🏗️ Architecture

```
Client
  │
  ▼
RateLimitingFilter (Bucket4j + Redis)
  │
  ▼
JwtAuthFilter (Spring Security)
  │
  ▼
TransactionFacade (Circuit Breaker)
  │
  ▼
TransactionService (Pessimistic Locking, Idempotency)
  │
  ├──▶ PostgreSQL (Accounts, Transactions)
  ├──▶ Redis (Cache, Idempotency, Rate Limiting)
  └──▶ Kafka ──▶ FraudDetectionConsumer
                      │
                      ▼
               FraudDetectionService
               (CompletableFuture, 3 Rules)
```

---

## 🔐 Authentication Flow

```
POST /api/auth/register  →  BCrypt password encoding  →  User saved
POST /api/auth/login     →  AuthenticationManager     →  JWT token returned
All other endpoints      →  JwtAuthFilter validates token
```

---

## ⚙️ Tech Stack

- **Java 21** + **Spring Boot 3.4.1**
- **PostgreSQL** — primary database
- **Redis** — caching, rate limiting, idempotency, distributed locking
- **Kafka** — event streaming for fraud detection
- **Docker** — Redis and Kafka containers
- **Resilience4j** — circuit breaker
- **Bucket4j** — rate limiting
- **Redisson** — distributed locking
- **JJWT** — JWT token generation and validation
- **JUnit 5 + Mockito** — unit testing

---

## 🛡️ Key Engineering Decisions

**Deadlock Prevention**
Pessimistic locks always acquired in ascending account ID order, regardless of transaction direction.

**Exactly-Once Kafka Processing**
Idempotent producer + `acks=all` + `read_committed` isolation + `accountId` as partition key ensures ordering and no duplicate processing.

**Circuit Breaker Placement**
`@CircuitBreaker` placed on `TransactionFacade` (outer layer) with `@Transactional` on `TransactionService` (inner layer) to ensure correct AOP proxy ordering.

**Idempotency Key Timing**
Key stored in Redis *after* successful processing — not before — so failed transactions can always be retried.

---

## 🧪 Running Locally

### Prerequisites
- Java 21
- PostgreSQL running on port 5432
- Docker (for Redis and Kafka)

### Start Infrastructure
```bash
docker-compose up -d
```

### Configure
Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/finflow
spring.datasource.username=your_username
spring.datasource.password=your_password
jwt.secretKey=your_base64_encoded_secret
jwt.expiration=600000
```

### Run
```bash
./mvnw spring-boot:run
```

---

## 📬 API Endpoints

### Auth
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Login and receive JWT |

### Accounts
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/accounts` | Create account |
| GET | `/api/accounts/{id}` | Get account by ID |

### Transactions
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/transactions` | Post a transaction |
| GET | `/api/transactions/{accountId}` | Get transactions (paginated) |

### Batch Jobs
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/batch` | Create batch job |

---

## 📁 Project Structure

```
src/main/java/com/example/finflow/
├── config/          # SecurityConfig, AsyncConfig, RedisConfig, AppConfig
├── controller/      # AuthController, TransactionController, AccountController
├── service/         # TransactionService, FraudDetectionService, JWTService...
├── filter/          # JwtAuthFilter, RateLimitingFilter
├── entity/          # User, Account, Transaction, BatchJob
├── repository/      # JPA Repositories
├── dto/             # Request/Response DTOs
├── exception/       # Custom exceptions + Global handler
└── mapper/          # Entity to DTO mappers
```

---

## 🧠 Phases Built

- ✅ Phase 1 — Entities, DTOs, Services
- ✅ Phase 2 — Batch Processing
- ✅ Phase 3 — Pessimistic Locking, Deadlock Prevention
- ✅ Phase 4 — Redis Caching, Pagination
- ✅ Phase 5 — CompletableFuture, Fraud Detection
- ✅ Phase 6 — Kafka, Exactly-Once Processing
- ✅ Phase 7 — Rate Limiting, Distributed Locking, Idempotency, Circuit Breaker
- ✅ Phase 8 — JWT Security
- ✅ Phase 9 — Unit Testing

---

*Built as a backend engineering learning project to simulate real-world fintech system design.*
