# Validation — Fix Mutation Testing Coverage

## Acceptance Criteria

- [ ] `./gradlew pitest` reports mutation score ≥ 80%
- [ ] `./gradlew build` passes (all unit and integration tests green)
- [ ] CI pipeline passes with the Pitest threshold enforced
- [ ] All surviving mutations from the initial report have been addressed (killed or documented as false positives)

## Manual Checks

- Run `./gradlew pitest` locally and confirm the mutation score in the HTML report
- Review new test files/methods to ensure they test meaningful behavior — each new test should verify a real requirement or edge case, not merely satisfy Pitest
- Verify no test runtime increase beyond a reasonable margin (compare before/after `./gradlew test` times)

## Regression Concerns

- All existing unit and integration tests must continue to pass unchanged
- No production code changes — only test additions/modifications
- Testcontainers-based integration tests against PostgreSQL must still pass
- The Gradle build must remain green in CI with no timeout or flakiness