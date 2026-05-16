# Testing Strategy

This document describes the testing strategy, how to run each test suite, and the coverage thresholds enforced by CI.

## Test Suites

### Unit Tests

Pure unit tests that run in isolation without a Spring context. These test individual classes (models, validators, formatters) using Mockito or direct instantiation.

```bash
# Run all unit tests
./gradlew test --tests "org.springframework.samples.petclinic.model.*"
./gradlew test --tests "org.springframework.samples.petclinic.owner.OwnerTests"
./gradlew test --tests "org.springframework.samples.petclinic.owner.PetValidatorTests"
./gradlew test --tests "org.springframework.samples.petclinic.owner.PetTypeFormatterTests"
./gradlew test --tests "org.springframework.samples.petclinic.vet.VetTests"
```

### WebMvc Slice Tests

`@WebMvcTest` slice tests that test controllers in isolation with MockMvc and mock dependencies. These test MVC and REST controller request handling without starting the full application.

```bash
# Run MVC controller tests
./gradlew test --tests "org.springframework.samples.petclinic.owner.OwnerControllerTests"
./gradlew test --tests "org.springframework.samples.petclinic.owner.PetControllerTests"
./gradlew test --tests "org.springframework.samples.petclinic.owner.VisitControllerTests"
./gradlew test --tests "org.springframework.samples.petclinic.vet.VetControllerTests"

# Run REST controller tests
./gradlew test --tests "org.springframework.samples.petclinic.rest.controller.*RestControllerTests"
./gradlew test --tests "org.springframework.samples.petclinic.rest.controller.GlobalExceptionHandlerTests"
```

### DataJpa Tests

`@DataJpaTest` slice tests that test repository and JPA layer against the H2 in-memory database.

```bash
./gradlew test --tests "org.springframework.samples.petclinic.service.ClinicServiceTests"
./gradlew test --tests "org.springframework.samples.petclinic.owner.OwnerRepositoryTests"
./gradlew test --tests "org.springframework.samples.petclinic.owner.PetTypeRepositoryTests"
./gradlew test --tests "org.springframework.samples.petclinic.vet.VetRepositoryTests"
```

### Integration Tests

Full `@SpringBootTest` integration tests that start the entire application. These use `TestRestTemplate` for HTTP-level testing against H2.

```bash
# Run REST integration tests
./gradlew test --tests "org.springframework.samples.petclinic.rest.controller.OwnerRestControllerIntegrationTests"
./gradlew test --tests "org.springframework.samples.petclinic.rest.controller.VetRestControllerIntegrationTests"

# Run contract tests
./gradlew test --tests "org.springframework.samples.petclinic.rest.contract.*"

# Run general integration tests
./gradlew test --tests "org.springframework.samples.petclinic.PetClinicIntegrationTests"
```

### Testcontainers Integration Tests (PostgreSQL)

Tests that run against a real PostgreSQL database using Testcontainers. These require Docker to be available.

- `PostgresIntegrationTests` — verifies repository and HTTP behavior against PostgreSQL
- Inherits from `BasePostgresIntegrationTest` which manages the Testcontainers lifecycle

```bash
# Requires Docker to be running
./gradlew test --tests "org.springframework.samples.petclinic.PostgresIntegrationTests"
```

These tests are automatically skipped if Docker is not available.

## Coverage Thresholds

JaCoCo is configured to enforce minimum coverage thresholds:

| Metric | Minimum |
|--------|---------|
| Line coverage | 80% |
| Branch coverage | 75% |

Coverage verification runs as part of `./gradlew check`. The JaCoCo HTML report is generated at `build/reports/jacoco/test/html/`.

## Mutation Testing

Pitest is configured for mutation testing. Run it separately (it takes longer than regular tests):

```bash
./gradlew pitest
```

The Pitest HTML report is generated at `build/reports/pitest/`. The mutation coverage threshold is set to 80%.

## Running All Tests

```bash
# Run all tests (excluding Pitest)
./gradlew check -x pitest

# Run all tests including Pitest (slow, Docker required for some tests)
./gradlew check

# Run Pitest separately
./gradlew pitest
```

## CI Pipeline

The GitHub Actions CI workflow (`ci.yml`) runs:

1. `./gradlew check` — builds, runs all tests, checks code style, and verifies JaCoCo coverage thresholds
2. `./gradlew pitest` — runs mutation testing (can be added to CI)
3. JaCoCo and Pitest reports are uploaded as artifacts