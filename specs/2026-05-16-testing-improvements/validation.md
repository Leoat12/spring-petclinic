# Validation — Testing Improvements

## Acceptance Criteria

- [ ] Integration tests cover all repository and service layers
- [ ] Testcontainers profile launches PostgreSQL and all integration tests pass against it
- [ ] Contract tests exist for every REST API endpoint (Owners, Pets, Visits, Vets)
- [ ] Pitest reports ≥ 80% mutation coverage
- [ ] JaCoCo enforces ≥ 80% line coverage and ≥ 75% branch coverage
- [ ] GitHub Actions CI runs: build, tests, JaCoCo verification, Pitest, contract tests
- [ ] `TESTING.md` documents the test strategy and commands

## Manual Checks

```bash
# Full build with all gates
./gradlew check

# Run Testcontainers-based PostgreSQL tests (requires Docker)
./gradlew test --tests "org.springframework.samples.petclinic.PostgresIntegrationTests"

# Run Pitest mutation testing
./gradlew pitest

# Check JaCoCo coverage report
./gradlew jacocoTestReport

# Verify coverage thresholds
./gradlew jacocoTestCoverageVerification
```

- Open the JaCoCo HTML report and verify line coverage ≥ 80% and branch coverage ≥ 75%
- Open the Pitest HTML report and verify mutation coverage ≥ 80%
- Verify GitHub Actions CI passes on a push to the feature branch

## Regression Concerns

- All existing integration and unit tests must continue to pass
- The existing REST API endpoints must return the same responses (contract tests guard this)
- H2 dev profile must still work for local development (`./gradlew bootRun`)
- The Thymeleaf UI must remain functional (no changes to web layer)
- CI build time should remain reasonable (Testcontainers startup cost is acceptable)