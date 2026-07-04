# AI Money Assistant — Backend

A Spring Boot service that uses an LLM (via Spring AI) to make personal-finance data smarter. Three AI capabilities over one PostgreSQL table:

1. **Extraction** — turn a free-text note (`"dinner at Barbeque Nation 2400"`) into a structured, categorized expense (Spring AI *structured output*).
2. **Chat** — answer natural-language questions about your spending by letting the LLM call read-only backend query methods (Spring AI *function calling*).
3. **Insights** — a short, cached monthly spending review.

> This is the backend/API stage. An Angular frontend is planned on top of these endpoints.

## Tech stack

Java 21 · Spring Boot 3.4 · Spring AI 1.0 (OpenAI-compatible API, using Google Gemini's free endpoint) · Spring Data JPA · PostgreSQL 16 · Flyway · Caffeine · springdoc (Swagger UI) · JUnit 5

> The model provider is swappable — Spring AI abstracts it. This build uses Google Gemini (free tier) via its OpenAI-compatible API; switching to Claude/OpenAI/Groq/Ollama is a config change, no code changes.

## Prerequisites

- JDK 21+ (tested compiling with a newer JDK targeting release 21)
- A local PostgreSQL 16 instance
- A free Google Gemini API key — get one at https://aistudio.google.com/apikey (no credit card required)

## Set up the database

Create the database and user once (using `psql` as a superuser):

```sql
CREATE DATABASE moneydb;
CREATE USER money WITH PASSWORD 'money';
GRANT ALL PRIVILEGES ON DATABASE moneydb TO money;
```

Adjust `spring.datasource.*` in `application.properties` if your host/port/credentials differ.

## Run it

```bash
# Provide your Gemini key (PowerShell)
$env:GEMINI_API_KEY = "AIza..."
#    ...or bash:
export GEMINI_API_KEY="AIza..."

# Run the app
mvn spring-boot:run
```

On first start, Flyway creates the schema and a handful of demo expenses are seeded
(so the chat has data to query). Then open **http://localhost:8080/swagger-ui.html**.

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/expenses` | Create from free text (AI extraction) |
| `GET`  | `/api/expenses?category=&from=&to=` | List / filter |
| `GET`  | `/api/expenses/summary?month=YYYY-MM` | Totals by category |
| `DELETE` | `/api/expenses/{id}` | Delete |
| `POST` | `/api/chat` | Natural-language Q&A (function calling) |
| `GET`  | `/api/insights?month=YYYY-MM` | Cached monthly review |

### Examples

```bash
# Extraction
curl -X POST localhost:8080/api/expenses \
  -H "Content-Type: application/json" \
  -d '{"text":"uber to the airport 620"}'
# -> { "merchant":"Uber", "amount":620.00, "category":"TRAVEL", ... }

# Chat (the LLM picks which query tool to run)
curl -X POST localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"How much did I spend on food this month?"}'
# -> { "answer":"You spent INR 2,880 on Food & Dining...", "toolsUsed":["getSpendingByCategory"] }
```

## How the AI pieces work

- **Extraction** (`ExtractionService`) — `chatClient...call().entity(ExtractedExpense.class)`
  maps the model's JSON straight into a typed record. `category` is a fixed enum, so
  the model can't invent categories; invalid output returns `422`, never `500`.
- **Chat** (`ChatService` + `QueryTools`) — the four `@Tool`-annotated methods are
  handed to the model, which decides which to call and with what arguments. Tools are
  **read-only**; the LLM never writes to the DB or sees raw SQL. Tool calls are traced
  and returned in `toolsUsed`.
- **Insights** (`InsightsService`) — numbers are computed in plain Java; the LLM only
  phrases them. Result is `@Cacheable` by month.

## Design choices worth noting

- **Function calling, not RAG** — the data is structured, so exact SQL beats semantic
  search. RAG would be the wrong tool here.
- **Graceful degradation** — an LLM failure in chat returns a friendly message, not an error.
- **One table** — Postgres is justified without over-modelling.

## Tests

```bash
mvn test
```
Pure-logic unit tests (month-range parsing, summary/percentage math, extraction
validation) run without a database or API key.

## Project layout

```
src/main/java/com/moneyassistant/
├── config/        AiConfig (ChatClient bean)
├── domain/        Expense, Category
├── repository/    ExpenseRepository
├── dto/           request/response records + LLM output shapes
├── service/       ExtractionService, ChatService, QueryTools, InsightsService, ExpenseService
├── web/           controllers + GlobalExceptionHandler
├── bootstrap/     DataSeeder (demo data)
└── util/          MonthRange
```

## Roadmap

- [ ] Angular frontend (sidebar: Expenses / Dashboard / Ask AI)
- [ ] Streaming chat responses (SSE) with a live tool-call trace
- [ ] User accounts / multi-user data scoping
- [ ] Deploy (Railway/Render) with a live demo link
