# Requirements — PostgreSQL Only

## Scope

Remove all MySQL compatibility from the project. PostgreSQL becomes the sole production database, while H2 remains the default for local development. Specifically:

- Remove MySQL driver dependency from Gradle build
- Remove MySQL-specific Spring profile configuration (`application-mysql.yml` or similar)
- Remove MySQL-specific schema and data initialization scripts
- Remove any MySQL-related properties scattered across config files
- Update Testcontainers integration to use PostgreSQL exclusively
- Update all documentation to reflect PostgreSQL as the only production database

**Out of scope:**
- No changes to the H2 dev configuration beyond ensuring compatibility
- No new features or schema changes
- No migration of JPA to JDBC (that's Phase 5)

## Decisions

- **H2/PostgreSQL schema in sync**: H2 and PostgreSQL schema scripts must remain structurally aligned. Both use separate dialect-specific files but represent the same logical schema and data. This ensures local development (H2) accurately reflects production (PostgreSQL).
- **Full removal, not deprecation**: MySQL artifacts are removed entirely — no commented-out config, no feature flags, no backward compatibility paths.
- **H2 stays default for dev**: The `dev` profile continues to use H2 for fast local iteration. Only the `production`/`ci` profile uses PostgreSQL.

## Context

- This project is a Spring Boot learning portfolio. Removing MySQL simplifies the codebase and reduces cognitive load for developers studying it.
- Phase 0 established Docker and CI, so PostgreSQL container images are already available in the pipeline.
- Phase 2 removed Maven, establishing a pattern of removing legacy tooling. This phase follows the same approach for MySQL.
- Spring Data JPA is still in use (JDBC migration comes in Phase 5), so JPA dialect configuration needs updating for PostgreSQL only.
- Testcontainers integration likely already uses PostgreSQL; verify and clean up any MySQL container references.
- Spring profiles are used to switch between H2 (default/dev) and PostgreSQL (production/ci). Maintain this profile-based approach.