# Requirements — Fix Mutation Testing Coverage

## Scope

Raise the Pitest mutation score from ~70% to at least 80% by adding or improving unit tests that kill surviving mutations. This phase is focused solely on killing mutations — no test refactoring or consolidation unless directly necessary to reach the threshold.

**In scope:**

- Analyzing the Pitest report to identify all surviving mutations across modules
- Adding or improving unit tests in service, repository, controller, utility, and configuration layers
- Ensuring Pitest enforces the 80% mutation threshold in CI

**Out of scope:**

- Refactoring or consolidating existing tests
- Adding new features or changing production code
- Reorganizing test structure or filenames
- Performance optimization of the test suite

## Decisions

- Focus on killing mutations rather than refactoring tests — new tests should be meaningful and test real behavior, not just "cheat" to kill mutants
- Follow the roadmap layer-by-layer approach: analyze first, then service/repo, then controllers, then utility/config
- The 80% threshold is the gate; once achieved, the phase is complete

## Context

- Pitest is already configured in the Gradle build and CI pipeline (added in Phase 5)
- The current mutation score is approximately 70%, which is below the 80% threshold
- The project uses Spring Data JDBC (migrated from JPA in Phase 6), so repository tests exercise explicit SQL
- This phase must complete before the Kotlin migration (Phase 8) begins, as Kotlin will shift the codebase significantly and surviving mutations would be harder to track
- H2 is the dev database; PostgreSQL via Testcontainers is used in CI integration tests