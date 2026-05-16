# Plan — Testing Improvements

## 1. Test Containers Profile

- [ ] Add Testcontainers dependency to the Gradle build
- [ ] Create a `testcontainers` Spring profile that launches a PostgreSQL container for integration tests
- [ ] Write a base test configuration class that authors can extend for Testcontainers-based tests
- [ ] Verify: `./gradlew test -Ptestcontainers` runs against a real PostgreSQL instance

## 2. Repository Integration Tests

- [ ] Expand integration tests for OwnerRepository (CRUD, queries, pagination)
- [ ] Expand integration tests for PetRepository (CRUD, by-owner queries)
- [ ] Expand integration tests for VisitRepository (CRUD, by-pet queries)
- [ ] Expand integration tests for VetRepository (CRUD, specialty associations)
- [ ] Expand integration tests for SpecialtyRepository (CRUD)
- [ ] Run repository tests against both H2 and Testcontainers PostgreSQL profiles

## 3. Service-Layer Integration Tests

- [ ] Expand integration tests for OwnerService (business logic, cascading operations)
- [ ] Expand integration tests for PetService (business logic, validation)
- [ ] Expand integration tests for VisitService (business logic, date constraints)
- [ ] Expand integration tests for VetService (business logic, specialty filtering)
- [ ] Expand integration tests for ClinicService (cross-domain operations)

## 4. Controller & REST API Contract Tests

- [ ] Add Spring Cloud Contract (or equivalent) dependency to the Gradle build
- [ ] Write contract definitions for Owner REST endpoints (CRUD, list, detail)
- [ ] Write contract definitions for Pet REST endpoints
- [ ] Write contract definitions for Visit REST endpoints
- [ ] Write contract definitions for Vet REST endpoints
- [ ] Generate and verify contract tests from definitions

## 5. Mutation Testing with Pitest

- [ ] Add Pitest plugin to the Gradle build
- [ ] Configure Pitest to target all main source modules
- [ ] Run initial Pitest report and assess baseline mutation coverage
- [ ] Add or improve tests to eliminate surviving mutations until zero survive
- [ ] Add Pitest step to the GitHub Actions CI workflow

## 6. Coverage Thresholds & CI Gates

- [ ] Configure JaCoCo to enforce ≥ 80% line coverage and ≥ 80% branch coverage
- [ ] Add JaCoCo verification task that fails the build below thresholds
- [ ] Add JaCoCo and Pitest gates to the GitHub Actions CI workflow
- [ ] Ensure `./gradlew build` passes only when all thresholds are met

## 7. Test Documentation

- [ ] Create `TESTING.md` in the project root describing the testing strategy
- [ ] Document how to run each test suite (unit, integration, Testcontainers, contract, mutation)
- [ ] Document coverage thresholds and how to check them locally
- [ ] Document CI test pipeline and what each gate enforces