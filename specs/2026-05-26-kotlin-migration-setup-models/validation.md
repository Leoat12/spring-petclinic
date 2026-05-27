# Validation — Kotlin Migration: Setup & Models

## Acceptance Criteria

- [ ] `./gradlew build` passes (compile + all tests)
- [ ] `./gradlew pitest` passes with mutation score ≥ 80%
- [ ] Application starts successfully with both H2 (`dev` profile) and PostgreSQL (Testcontainers)
- [ ] All five model entities (Owner, Pet, Visit, Vet, Specialty) are Kotlin data classes
- [ ] No Java source files remain for the migrated model classes
- [ ] Mixed Java/Kotlin compilation works — remaining Java code compiles against Kotlin models
- [ ] Spring Data JDBC maps Kotlin data classes correctly (CRUD operations succeed)
- [ ] Thymeleaf views render correctly (owner list, vet list, pet details, etc.)
- [ ] REST API endpoints return correct JSON for all domain resources

## Manual Checks

- Start the app with `./gradlew bootRun` and verify:
  - Home page loads
  - `/owners` list page renders with owner data
  - `/vets.html` renders with vet data
  - Owner detail page shows pets and visits
- Run `curl localhost:8080/api/v1/owners` and verify JSON response
- Run `curl localhost:8080/api/v1/vets` and verify JSON response
- Confirm `src/main/kotlin/` contains the migrated model files
- Confirm `src/main/java/` no longer contains the migrated model files

## Regression Concerns

- **REST API contract**: JSON output must remain unchanged (field names, structure, status codes)
- **Thymeleaf views**: All existing UI pages must work without modification
- **Spring Data JDBC repositories**: Must correctly persist and retrieve Kotlin data classes
- **Test suite**: All existing integration and contract tests must pass
- **Mutation testing**: Pitest score must remain ≥ 80%
- **Build tooling**: Checkstyle, JaCoCo, and CI pipeline must work with mixed Java/Kotlin sources