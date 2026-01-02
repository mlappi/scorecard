# AGENTS.md — Golf Scorecard App

This file provides **actionable instructions for agentic coding agents** operating in this repository. Follow these rules unless a higher‑priority system or user instruction overrides them.

---
## Project Overview

- Spring Boot MVC web application (Java 21), packaged as an **executable WAR**.
- Server-side rendering with **Thymeleaf**.
- Layered architecture:
  - `controller/` — HTTP routes, view selection, form handling
  - `service/` — business logic and transactional behavior
  - `repository/` — Spring Data JPA repositories
  - `model/` — JPA entities
- Static assets: `src/main/resources/static`
- Templates: `src/main/resources/templates`

Controllers → Services → Repositories is the intended dependency direction.

---
## Build, Run, Test

### Prerequisites

- Java **21** (see `pom.xml`)
- Maven Wrapper or system Maven
- Docker (optional, for Postgres)

### Common Commands

- Run locally (default HSQLDB / dev profile):
  ```
  mvn spring-boot:run
  ```

- Run with Postgres profile:
  ```
  docker compose up -d
  SPRING_PROFILES_ACTIVE=postgres mvn spring-boot:run
  ```

- Build WAR (skip tests, same as Dockerfile):
  ```
  mvn -DskipTests package
  ```

- Build Docker image:
  ```
  docker build -t golfapp .
  ```

### Testing

- UI smoke coverage lives in `src/test/java/fi/mlappi/golf/ApplicationUiTest.java` and boots the app with profile `test`.
- Run all tests:
  ```
  mvn test
  ```
- Run a single class:
  ```
  mvn -Dtest=ApplicationUiTest test
  ```
- Run a single method:
  ```
  mvn -Dtest=ApplicationUiTest#adminLoginPageShowsCredentialsForm test
  ```
- Tests rely on the in-memory HSQL profile declared in `src/test/resources/application-test.properties`. Do not point them to Postgres.

### Linting / Formatting

- No explicit linting tools (Checkstyle, Spotless, etc.) are configured.
- Follow existing formatting and naming conventions strictly.

---
## Configuration & Profiles

- Properties files:
  - `application.properties` — default / dev
  - `application-postgres.properties` — Postgres profile
- Active profile may be set **inside** properties files; always verify before running.
- Postgres profile expects env vars:
  - `POSTGRES_USER`
  - `POSTGRES_PASSWORD`
  - `POSTGRES_DB`

---
## Security & Admin Model

- Admin access is a **simple session flag**, not Spring Security.
- Credentials configured via:
  - `admin.username`
  - `admin.password`
- Admin-protected routes are enforced by:
  - `config/AdminAuthInterceptor.java`
  - `config/WebConfig.java`
- If you add or change endpoints:
  - Update interceptor path patterns accordingly.

---
## Critical Business Logic

- `ScorecardService.save()` uses an **in-memory per (roundId, playerId) lock**
  to prevent duplicate inserts.
- Any refactor of score saving **must preserve this behavior** or replace it
  with an equivalent DB-level constraint + safe upsert logic.

---
## Code Style Guidelines

### General

- Prefer **clarity over cleverness**.
- Keep changes **minimal and localized**.
- Follow existing patterns; do not introduce new architectural styles.

### Packages & Imports

- Use explicit imports; no wildcard imports.
- Package naming follows reverse domain: `fi.mlappi.golf.*`.
- Do not move classes between layers without strong reason.

### Formatting

- 4-space indentation.
- One class per file.
- Braces on the same line (`K&R` style).
- Keep methods short and readable.

### Naming

- Classes: `PascalCase`
- Methods and variables: `camelCase`
- Constants: `UPPER_SNAKE_CASE`
- Controller methods should describe user intent (`listGames`, `newScorecard`).

### Controllers

- Return **view name strings** matching template filenames exactly.
- Use `@Valid` + `BindingResult` for form validation.
- Avoid business logic; delegate to services.

### Services

- Encapsulate transactional and concurrency-sensitive logic here.
- Avoid accessing HTTP/session state directly.
- Prefer constructor injection.

### Repositories

- Extend Spring Data interfaces (`CrudRepository`, etc.).
- Use **method-name queries**; keep names stable.
- Do not add custom SQL unless necessary.

### Entities

- Use JPA annotations consistently.
- Avoid heavy logic in entities.
- Lombok is allowed and already configured.

### Error Handling

- Prefer graceful handling with user-facing feedback (redirect + message).
- Avoid throwing raw exceptions to the UI layer.
- For invalid input, return to the form view with validation errors.

---
## Templates & UI

- Thymeleaf templates must match controller return names.
- Keep logic minimal; no complex conditionals.
- Reuse `layout.html` where applicable.

---
## Database & Schema

- Dev profile uses `ddl-auto=create`.
- Postgres profile uses `ddl-auto=update`.
- Be cautious with schema changes; they affect live data in Postgres.

---
## Docker & Deployment

- Multi-stage Dockerfile builds WAR with Maven, runs with JRE.
- `Procfile` defines production run command (Render / Heroku-style).

---
## Copilot / Cursor Rules

- Follow all guidance in:
  - `.github/copilot-instructions.md`
- No `.cursor/rules` or `.cursorrules` files exist in this repo.
- If rules conflict, **Copilot instructions take precedence**.

---
## Safe Defaults for Agents

- Do not add tests unless asked.
- Do not change build tooling.
- Do not introduce new frameworks.
- When unsure, inspect similar existing code and copy the pattern.

---
## When in Doubt

- Trace flow: Controller → Service → Repository.
- Search for similar behavior before implementing new logic.
- Prefer consistency with existing code over theoretical best practices.
