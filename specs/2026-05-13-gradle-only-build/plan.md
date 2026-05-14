# Plan — Gradle-Only Build

## 1. Verify Gradle Parity

- [ ] Audit the existing Maven `pom.xml` for all plugins and their configurations (Spring Boot, format, checkstyle, JaCoCo, SBOM, test)
- [ ] Compare each Maven plugin against the current `build.gradle` to identify gaps
- [ ] Fill in any missing Gradle plugin configurations (formatting, checkstyle, JaCoCo, etc.)
- [ ] Ensure `./gradlew build` produces a runnable JAR equivalent to the Maven output
- [ ] Ensure `./gradlew test` runs the same test suite as Maven

## 2. Remove Maven Files

- [ ] Delete `pom.xml`
- [ ] Delete `mvnw` and `mvnw.cmd`
- [ ] Delete `.mvn/` directory (if present)
- [ ] Remove any Maven-specific properties or profiles from `.gitignore` and other config files
- [ ] Search for any other Maven references in the repository and clean them up

## 3. Update CI Workflow

- [ ] Update the GitHub Actions workflow to remove any Maven steps or references
- [ ] Ensure CI runs `./gradlew build` (or equivalent) as the sole build command
- [ ] Ensure CI runs `./gradlew test` for testing
- [ ] Ensure the container image build in CI uses Gradle exclusively

## 4. Update Documentation and Scripts

- [ ] Update `README.md` to reference Gradle commands only (remove Maven instructions)
- [ ] Update `CONTRIBUTING.md` (if present) to reference Gradle commands
- [ ] Update any shell scripts or Makefiles that reference Maven
- [ ] Update any inline documentation or comments referencing Maven