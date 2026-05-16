# Requirements — Testing Improvements

## Scope

**In scope:**

- Expand integration tests for all repository and service layers
- Add Testcontainers-based integration test profile for PostgreSQL
- Configure Pitest (mutation testing) and add to CI
- Add contract tests for the REST API endpoints
- Enforce minimum line and branch coverage thresholds (≥ 80%)
- Require zero surviving mutations before merge
- Add test documentation describing the testing strategy and how to run each suite

**Out of scope:**

- Performance/load testing
- UI/Thymeleaf end-to-end testing (covered in later phases)
- Migration to Spring Data JDBC (Phase 6)
- Kotlin migration (Phase 7+)

## Decisions

- **Bottom-up approach**: build tests from the data layer upward — repositories first, then services, then controllers — so each layer has a solid foundation before dependent layers are tested.
- **Strict thresholds**: line and branch coverage must be ≥ 80%; Pitest must report zero surviving mutations. These gates are enforced in CI.
- **Testcontainers for PostgreSQL**: real database instances instead of in-memory H2 for integration tests, ensuring production parity.
- **Contract tests for API stability**: Spring Cloud Contract or a similar framework to guard against breaking API changes.
- **Test documentation**: a `TESTING.md` or equivalent section describing the testing strategy, how to run each suite, and what each threshold means.

## Context

- The project uses Spring Boot 4.0.3 with Spring Data JPA (migrating to JDBC later).
- Build is Gradle-only; all test tasks must be Gradle tasks.
- H2 is the dev profile database; PostgreSQL 18.3 is the production database. Testcontainers tests validate PostgreSQL compatibility.
- Existing CI runs on GitHub Actions; Pitest and coverage gates must integrate into that pipeline.
- The REST API layer (Phase 1) has integration tests already; this phase expands coverage to all layers and adds contract and mutation testing.