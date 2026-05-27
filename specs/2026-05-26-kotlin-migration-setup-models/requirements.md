# Requirements — Kotlin Migration: Setup & Models

## Scope

Migrate all five domain model classes (Owner, Pet, Visit, Vet, Specialty) from Java to Kotlin data classes, and add Kotlin build support to the Gradle configuration.

**In scope:**
- Add Kotlin plugin and dependencies to the Gradle build
- Convert Owner, Pet, Visit, Vet, and Specialty Java classes to Kotlin data classes
- Update Spring Data JDBC `@Table` / `@Id` mappings for Kotlin conventions
- Ensure mixed Java/Kotlin compilation works during the incremental migration

**Out of scope:**
- Migrating repository, service, or controller layers (Phase 10+)
- Migrating configuration classes (Phase 12)
- Changing the REST API contract or Thymeleaf views
- Adding new features or fields to existing models

## Decisions

- **Kotlin data classes** will be used for all model classes to leverage idiomatic Kotlin (auto-generated `equals`, `hashCode`, `toString`, `copy`).
- **Nullability** will be expressed via Kotlin's type system (nullable `Type?` for optional fields, non-null `Type` for required fields).
- **Mixed compilation**: Java and Kotlin source files will coexist during this phase; the Gradle build must support both until the full migration completes.
- **No API changes**: The REST API and Thymeleaf views must behave identically before and after this migration.

## Context

- This project uses **Spring Data JDBC** (migrated from JPA in Phase 6). Models use `@Table` and `@Id` annotations, not JPA annotations.
- The project runs on **Java 21** and **Spring Boot 4.0.3** with **Gradle 9.2.1**.
- H2 is the dev database; PostgreSQL is production. Both must continue to work.
- Mutation testing is enforced (Pitest, ≥80% threshold). The Kotlin migration must not regress the mutation score.
- The mission emphasizes **idiomatic, reference-quality code** — Kotlin models should follow Kotlin conventions, not just be transliterated Java.