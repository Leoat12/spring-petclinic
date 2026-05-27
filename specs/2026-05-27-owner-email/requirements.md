# Requirements — Owner Email Feature

## Scope

Add an `email` field to the `Owner` domain entity and expose it through both the REST API and the Thymeleaf UI.

**In scope:**
- Add `email` column to the `owners` table (H2 and PostgreSQL schema migrations)
- Add `email` property to `Owner` Kotlin data class
- Add `email` to the Owner DTO, REST controller, and request/response payloads
- Add `email` validation (well-formed email format when provided, optional field)
- Update Thymeleaf owner forms (create/edit) and list/detail views to show email
- Add unit, integration, contract, and mutation tests for the new field

**Out of scope:**
- Email sending or notification features
- Owner search or lookup by email
- Changes to Pet, Visit, Vet, or Specialty entities
- Vue.js frontend (that comes in later phases)

## Decisions

- **Email is optional.** Owners can be created and edited without providing an email address. The database column should be nullable, and the DTO field should accept `null` or empty.
- **Validation is applied when present.** If an email value is supplied, it must be a well-formed email address (e.g., via `@Email` constraint). Blank or null values are acceptable.
- **Backward compatibility.** Existing owner records have no email; the feature must not break them. Seed data scripts should be updated to include sample emails for some (not all) owners to demonstrate the optional nature.

## Context

- This is the first feature built on the Kotlin models + Spring Data JDBC foundation established in Phases 6–8. It validates that the new stack can handle a straightforward schema change end-to-end.
- The `Owner` entity is a Kotlin data class mapped via Spring Data JDBC `@Table`/`@Id` conventions.
- Schema migrations must work on both H2 (dev profile) and PostgreSQL (CI/Testcontainers). H2 and PostgreSQL DDL scripts live in `src/main/resources/db/` and are differentiated by Spring profile.
- The REST API lives under `/api/v1/owners` and uses DTOs to avoid leaking internal entities.
- Thymeleaf templates are still the active UI layer (Vue.js migration comes later).
- Mutation testing (Pitest) is enforced in CI with an 80% threshold — new code must maintain this.