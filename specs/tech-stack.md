# Tech Stack

## Current Stack

| Layer | Technology | Version                                  |
|-------|-----------|------------------------------------------|
| Runtime | Java | 21                                       |
| Framework | Spring Boot | 4.0.3                                    |
| Template Engine | Thymeleaf | (managed by Spring Boot)                 |
| Persistence | Spring Data JPA | (managed by Spring Boot)                 |
| Databases | H2 (dev), MySQL, PostgreSQL | H2 in-memory; MySQL 9.6; PostgreSQL 18.3 |
| Cache | Caffeine + Spring Cache | (managed by Spring Boot)                 |
| Validation | Bean Validation (Hibernate Validator) | (managed by Spring Boot)                 |
| Monitoring | Spring Boot Actuator | (managed by Spring Boot)                 |
| CSS Framework | Bootstrap | 5.3 (via WebJars)                        |
| Icons | Font Awesome | 4.7 (via WebJars)                        |
| Build | Maven / Gradle | Maven primary, Gradle secondary          |
| Code Style | Spring Java Format | 0.0.47                                   |
| Code Quality | Checkstyle, JaCoCo | Checkstyle 12.3.1, JaCoCo 0.8.14         |
| SBOM | CycloneDX | (managed by Spring Boot)                 |
| Container Build | Spring Boot build-image | (no Dockerfile)                          |

## Planned Additions

| Layer | Technology | Purpose |
|-------|-----------|---------|
| REST API | Spring Boot Starter WebMVC (JSON) | Expose domain resources as REST endpoints (API-first) |
| Frontend | Vue.js | Modern SPA frontend consuming the REST API |
| Containerization | Dockerfile | Reproducible container builds for deployment |
| CI/CD | GitHub Actions | Automated build, test, and container publish pipelines |
| Integration Testing | Spring Boot Test + expanded test coverage | Thorough integration and slice tests for all layers |
| Database Testing | Testcontainers | Realistic DB integration tests (MySQL, PostgreSQL) |
| Mutation Testing | Pitest | Detect untested code paths |
| Contract Testing | Spring Cloud Contract or similar | Verify REST API contracts stay stable |

## Tech Decisions

- **API-first**: REST endpoints are built before the Vue.js frontend so the API is the single source of truth.
- **Thymeleaf retained**: Existing Thymeleaf views remain functional during the transition; Vue.js views replace them incrementally.
- **H2 for dev**: In-memory H2 stays the default for rapid local development; MySQL and PostgreSQL profiles continue for integration tests and production-like environments.
