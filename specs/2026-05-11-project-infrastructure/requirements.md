# Requirements — Project Infrastructure

## Scope

**In scope:**

- Multi-stage `Dockerfile` for reproducible container builds
- `.dockerignore` to keep the Docker context lean
- GitHub Actions workflow: build, test, lint, and publish container image
- Code style enforcement (Checkstyle with Spring Java Format) integrated into CI
- Dependency vulnerability scanning (OWASP Dependency-Check or Dependabot) in CI
- Local Docker verification: the app runs correctly inside the built container

**Out of scope:**

- Multi-architecture builds (e.g., ARM64) — can be added later
- Kubernetes or deployment manifests
- Any application code changes

## Decisions

- Use a multi-stage Dockerfile that builds with Maven and runs with a JRE-only base image (smaller attack surface, faster pulls).
- Use GitHub-native Dependabot for dependency vulnerability scanning rather than a separate OWASP plugin — simpler configuration, native GitHub integration.
- Checkstyle is already configured in the project (`checkstyle.xml`, Spring Java Format 0.0.47); CI should enforce it as a gate.
- JaCoCo is already present; CI should enforce a minimum coverage threshold or at least report coverage.
- The existing Maven wrapper (`./mvnw`) should be used in both Dockerfile and CI for reproducibility.
- Spring Boot build-image (`./mvnw spring-boot:build-image`) currently exists; the new Dockerfile approach replaces it for more control over the build and runtime layers.

## Context

- **Java 21**, **Spring Boot 4.0.3**, **Maven** (primary build tool)
- The project already has Checkstyle and JaCoCo plugins configured
- The project uses Spring Boot's built-in `build-image` goal for container builds today — this phase introduces an explicit `Dockerfile` for more reproducibility and control
- H2 is the dev database; MySQL and PostgreSQL profiles exist for integration testing
- This is a learning/portfolio project — the Dockerfile and CI pipeline should be clear and well-commented so others can learn from them
- The existing Thymeleaf UI must continue to work unchanged after these infrastructure changes