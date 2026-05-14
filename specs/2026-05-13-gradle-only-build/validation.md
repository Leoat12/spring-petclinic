# Validation — Gradle-Only Build

## Acceptance Criteria

- [ ] `pom.xml` does not exist in the repository
- [ ] `mvnw`, `mvnw.cmd`, and `.mvn/` directory do not exist in the repository
- [ ] `./gradlew build` succeeds and produces a runnable JAR artifact
- [ ] `./gradlew test` runs all tests and they pass
- [ ] `./gradlew checkstyleMain` and `checkstyleTest` pass (code style enforced)
- [ ] `./gradlew jacocoTestReport` generates a coverage report
- [ ] Spring Java Format check (if configured) passes via `./gradlew check`
- [ ] No Maven commands or references remain in CI workflow files
- [ ] No Maven commands or references remain in README, contributing guides, or scripts

## Manual Checks

- Run `./gradlew build` — should compile, run tests, and produce a JAR in `build/libs/`
- Run `./gradlew check` — should run all quality checks (checkstyle, tests, etc.) and pass
- Run the JAR: `java -jar build/libs/spring-petclinic-*.jar` — application should start on port 8080
- Verify the CI pipeline passes after pushing to the branch
- Grep the repository for `mvn`, `maven`, `pom.xml` to confirm no references remain (excluding git history)
- Verify the existing REST API endpoints return correct JSON (e.g., `curl /api/v1/owners`)
- Verify the Thymeleaf UI loads correctly in a browser

## Regression Concerns

- All existing integration tests must continue to pass
- The Docker/container build process must still work (currently Gradle-based from Phase 0)
- The REST API endpoints must return the same responses
- The Thymeleaf UI must render correctly
- JaCoCo coverage reporting must still function
- Checkstyle must still enforce code style rules