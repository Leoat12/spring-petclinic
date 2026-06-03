# Requirements — Feedback Form Feature

## Scope

**In scope:**
- `Feedback` entity as a standalone Kotlin data class (name, email, message, createdAt)
- Schema migration for both H2 (dev) and PostgreSQL
- Repository and service layers using Spring Data JDBC idioms (Kotlin)
- REST API endpoint: `POST /api/v1/feedback` to submit feedback
- REST API endpoint: `GET /api/v1/feedback` for admin listing
- Thymeleaf form page for submitting feedback
- Thymeleaf admin page listing all submitted feedback
- Bean Validation on all fields (non-blank name, email format, message length constraints)

**Out of scope:**
- Email notifications on submission
- Rate limiting
- Authentication or owner relationship — feedback is standalone and anonymous
- Vue.js frontend (handled in Phase 17)

## Decisions

- **Standalone entity**: `Feedback` has no relationship to `Owner` or any other domain entity. Anyone can submit feedback without authentication.
- **Admin view**: A simple read-only list view for admins to review submitted feedback. No admin role/auth is enforced — this is a learning project, not production software.
- **Single POST endpoint**: Only `POST /api/v1/feedback` for creation and `GET /api/v1/feedback` for listing. No update or delete operations.
- **Kotlin-first**: All new code (entity, repository, service) will be in Kotlin, consistent with Phase 8–10 migration work.

## Context

- This is the second feature from the original TODO list, building on the Kotlin + Spring Data JDBC foundation established in Phases 8–10.
- Follow the same patterns used in the Owner Email feature (Phase 9): DTOs, `@RestController`, validation, and Thymeleaf integration.
- The project uses Spring Data JDBC (not JPA) with `JdbcClient`-based or `CrudRepository` implementations.
- H2 is the dev database; PostgreSQL is the production database. Schema migrations must be compatible with both.
- Pitest mutation testing is enforced in CI with an 80% threshold — all new code must be covered.