# FocusArc Backend

REST API for FocusArc, a productivity application. 
Users organize focused work sessions through a three-level hierarchy: **Arc** (a multi-week goal) → **Chapter** (one calendar day) → **Task** (a timed work block).

📖 **[Full documentation](https://focus-arc-docs-8udonll03-valentindeoliveiras-projects.vercel.app/docs/)**

---

## Tech stack

| Layer     | Technology                                   |
|-----------|----------------------------------------------|
| Language  | Java 17                                      |
| Framework | Spring Boot 4.0.2                            |
| Database  | MongoDB 6.0                                  |
| Build     | Maven                                        |
| Testing   | JUnit 5 · Mockito · Testcontainers · AssertJ |
| Coverage  | JaCoCo                                       |
| Dev DB    | Docker Compose                               |

---

## Prerequisites

- Java 17+
- Maven 3.3+
- Docker (for the local MongoDB)

---

## Run locally

**1. Start MongoDB**
```bash
docker compose up -d
```

**2. Set environment variables** — copy the example file and fill in the values:
```bash
cp .env.example .env
# edit .env — the defaults work for local dev as-is
```

**3. Start the API**
```bash
./mvnw spring-boot:run
```

The API is available at `http://localhost:8080/api`.  
Swagger UI: `http://localhost:8080/api/swagger-ui.html`

---

## Run the tests

```bash
./mvnw test
```

Requires Docker — integration tests spin up a real MongoDB via Testcontainers.

---

## Environment variables

All variables are **required** — the app will refuse to start if any is missing.  
See `.env.example` for local dev values.

| Variable               | Description                                                                     |
|------------------------|---------------------------------------------------------------------------------|
| `JWT_SECRET`           | HS256 signing key (min 256 bits).                                               |
| `MONGODB_URI`          | Full MongoDB connection string.                                                 |
| `GOOGLE_CLIENT_ID`     | Google OAuth 2.0 client ID.                                                     |
| `CORS_ALLOWED_ORIGINS` | Comma-separated list of allowed frontend origins (e.g. `https://focusarc.com`). |