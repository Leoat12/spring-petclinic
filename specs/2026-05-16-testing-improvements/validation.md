# Validation — Testing Improvements

## Acceptance Criteria

- [ ] Integration tests cover all repository and service layers
- [ ] Testcontainers profile launches PostgreSQL and all integration tests pass against it
- [ ] Contract tests exist for every REST API endpoint (Owners, Pets, Visits, Vets)
- [ ] Pitest reports zero surviving mutations
- [ ] JaCoCo enforces ≥ 80% line coverage and ≥ 80% branch coverage
- [ ] GitHub Actions CI runs: build, tests, JaCoCo verification, Pitest, contract tests
- [ ] `README.md` or `TESTING.md` documents the test strategy and commands

## Manual Checks

```bash
# Full build with all gates
./gradlew build

# Run only integration tests
./gradlew integrationTest

# Run Testcontainers-based tests
./gradlew test -Ptestcontainers

# Run Pitest mutation testing
./gradlew pitest

# Check JaCoCo coverage report
./gradlew jacocoTestReport

# Run contract tests
./gradlew contractTest
```

- Open the JaCoCo HTML report and verify line and branch coverage ≥ 80%
- Open the Pitest HTML report and verify zero surviving mutations
- Verify GitHub Actions CI passes on a push to the feature branch

## Regression Concerns

- All existing integration and unit tests must continue to pass
- The existing REST API endpoints must return the same responses (contract tests guard this)
- H2 dev profile must still work for local development (`./gradlew bootRun`)
- The Thymeleaf UI must remain functional (no changes to web layer)
- CI build time should remain reasonable (Testcontainers startup cost is acceptable)