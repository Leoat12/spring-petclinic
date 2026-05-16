# Validation — PostgreSQL Only

## Acceptance Criteria

- [ ] No MySQL driver dependency exists in `build.gradle`
- [ ] No `application-mysql.yml` or MySQL profile properties exist
- [ ] No MySQL schema or data scripts exist in the project
- [ ] No MySQL Testcontainers dependencies or container references exist
- [ ] `./gradlew build` succeeds with all tests passing
- [ ] Application starts and functions correctly with H2 (dev profile)
- [ ] Testcontainers-based integration tests pass using PostgreSQL exclusively
- [ ] CI pipeline runs green using only PostgreSQL (no MySQL service)
- [ ] No references to `mysql` or `MySQL` remain in source code, config, or docs (except historical/educational context if applicable)

## Manual Checks

- `./gradlew build` — full build and test suite passes
- `./gradlew bootRun` — application starts locally with H2 defaults
- `rg -i mysql src/` — returns no results (or only clearly non-functional references)
- `rg -i mysql specs/` — tech-stack.md updated to reflect MySQL removal
- CI workflow on the branch passes without MySQL service

## Regression Concerns

- **H2 dev profile**: Must continue to work as the default local development database without any changes to developer workflow
- **PostgreSQL CI/Testcontainers**: Must continue to work for integration testing; no regression in test coverage or reliability
- **Existing REST API**: All endpoints from Phase 1 must still function correctly with both H2 and PostgreSQL
- **Thymeleaf UI**: Existing server-rendered views must continue to work unchanged
- **Schema initialization**: Both H2 and PostgreSQL must initialize the database correctly on startup with their respective scripts