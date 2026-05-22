# Requirements — Migrate to Spring Data JDBC

## Scope

Migrate all domain entities (Owner, Pet, Visit, Vet, Specialty) from Spring Data JPA to Spring Data JDBC in a single pass. This covers:

- Replacing JPA annotations (`@Entity`, `@ManyToOne`, etc.) with Spring Data JDBC annotations (`@Table`, `@Id`, `@MappedCollection`, etc.)
- Replacing JPA repositories with Spring Data JDBC `CrudRepository` interfaces and `JdbcClient`-based implementations for custom queries
- Updating service and controller layers to work with the new repository interfaces
- Removing all Spring Data JPA dependencies and configuration

**Out of scope:**

- No Kotlin migration (that's Phase 7)
- No new features or endpoints — the REST API contract must remain unchanged
- No Vue.js or frontend changes

## Decisions

- **CrudRepository + JdbcClient**: Use Spring Data JDBC `CrudRepository` for standard CRUD operations and `JdbcClient` for custom/complex queries (e.g., N+1 query scenarios, joins, aggregations).
- **N+1 queries**: Address the N+1 problem that JPA lazy-loading previously handled by using `JdbcClient` to write explicit join queries or batch-fetch related entities as needed.
- **All entities at once**: Migrate all five domain entities simultaneously rather than domain-by-domain, since the JPA dependency removal is a clean cut.
- **H2 + PostgreSQL compatibility**: Both H2 (dev profile) and PostgreSQL (production/Testcontainers) must remain functional. All SQL must be compatible with both databases.
- **API contract preserved**: The REST API endpoints must return the same JSON shapes and status codes.

## Context

- The project uses Spring Boot 4.0.3 with Spring Data JPA currently.
- H2 is the default `dev` profile database; PostgreSQL is used in CI/Testcontainers.
- The existing REST API layer (Phase 1) must continue to work without any breaking changes to request/response formats.
- This migration is a prerequisite for the Kotlin migration (Phase 7), which will convert these entities to Kotlin data classes.
- Spring Data JDBC requires explicit SQL for relationships — there is no lazy-loading or dirty-checking. This means all relationship fetching must be intentional.
- The project follows Spring Java Format for code style (version 0.0.47).