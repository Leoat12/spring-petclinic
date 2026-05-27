# Roadmap

Small phases, each deliverable and testable on its own.

---

## Phase 0 — Project Infrastructure

Set up containerization and CI so every subsequent phase has automated verification.

- [x] Add a multi-stage `Dockerfile` for reproducible container builds
- [x] Add `.dockerignore`
- [x] Add GitHub Actions workflow: build, test, and publish container image
- [x] Verify: pushing to `main` triggers a green CI run

---

## Phase 1 — REST API Layer

Expose the existing domain (Owners, Pets, Visits, Vets, Specialties) as JSON resources. API-first: no UI changes yet.

- [x] Add `@RestController` classes alongside existing `@Controller` classes for each domain (Owner, Pet, Visit, Vet)
- [x] Use Spring Data JPA projections/DTOs to avoid leaking internal entities
- [x] Add proper HTTP status codes, error responses, and pagination where appropriate
- [x] Add integration tests for every endpoint
- [x] Verify: `curl /api/v1/owners` returns JSON; existing Thymeleaf UI still works unchanged

---

## Phase 2 — Gradle-Only Build

Remove Maven support; standardize on Gradle as the sole build tool.

- [x] Remove `pom.xml` and all Maven-specific configuration
- [x] Ensure the Gradle build covers all functionality previously handled by Maven (build, test, format, checkstyle, JaCoCo, etc.)
- [x] Update CI workflow to use Gradle exclusively
- [x] Update documentation and scripts to reference Gradle commands only
- [x] Verify: `./gradlew build` produces the same artifact; CI passes with Gradle only

---

## Phase 3 — PostgreSQL Only

Remove MySQL compatibility; PostgreSQL becomes the sole production database. H2 stays for local development speed.

- [x] Remove MySQL driver dependency and profile-specific configuration
- [x] Remove MySQL-specific schema and data scripts
- [x] Update Testcontainers integration tests to use PostgreSQL only
- [x] Keep H2 as the default `dev` profile for fast local development
- [x] Update documentation to reflect PostgreSQL as the sole production database
- [x] Verify: application starts and all tests pass with H2 (dev) and PostgreSQL (CI/Testcontainers) only; no MySQL artifacts remain

---

## Phase 4 — IntelliJ HTTP Client File

Add a `.http` file covering all existing REST API endpoints so developers can exercise the API directly from IntelliJ IDEA after each feature.

- [x] Create `petclinic.http` in the project root with requests for all 14 REST endpoints
- [x] Use sample data IDs and payloads matching the H2 seed data
- [x] Organize requests by domain (Owners, Pets, Visits, Vets) with clear comments
- [x] Verify: every request in the file runs successfully against a fresh local `dev` profile

---

## Phase 5 — Testing Improvements

Harden the test suite on the simplified database setup before deeper migrations.

- [x] Expand integration tests for all repository and service layers
- [x] Add Testcontainers-based integration test profile for PostgreSQL
- [x] Configure Pitest (mutation testing) and add to CI
- [x] Add contract tests for the REST API endpoints
- [x] Verify: mutation coverage threshold enforced; contract tests pass in CI

---

## Phase 6 — Migrate to Spring Data JDBC

Replace Spring Data JPA with Spring Data JDBC for explicit SQL queries and better control.

- [x] Add Spring Data JDBC dependency; remove Spring Data JPA dependency
- [x] Migrate each entity to Spring Data JDBC `@Table`/`@Id` conventions (no JPA annotations)
- [x] Replace JPA repositories with `JdbcClient`-based or `CrudRepository` implementations using explicit SQL
- [x] Write and verify SQL migrations for PostgreSQL and H2 compatibility
- [x] Update all service and controller layers for the new repository interfaces
- [x] Verify: all existing integration and contract tests pass with JDBC repositories; no JPA artifacts remain

---

## Phase 7 — Fix Mutation Testing Coverage

Raise Pitest mutation score from 70% to meet the 80% threshold before proceeding with further migrations.

- [x] Analyze Pitest report to identify surviving mutations across all modules
- [x] Add or improve unit tests to kill surviving mutations in service and repository layers
- [x] Add or improve unit tests to kill surviving mutations in controller layers
- [x] Add or improve unit tests to kill surviving mutations in utility and configuration classes
- [x] Re-run Pitest and verify mutation score is at or above the 80% threshold
- [x] Verify: `./gradlew pitest` passes with mutation score ≥ 80; CI pipeline green

---

## Phase 8 — Kotlin Migration: Setup & Models

Add Kotlin support to the project and migrate the domain model layer.

- [x] Add Kotlin plugin and dependencies to the Gradle build
- [x] Migrate entity/model classes (Owner, Pet, Visit, Vet, Specialty) to Kotlin data classes
- [x] Update Spring Data JDBC mappings for Kotlin data classes
- [x] Verify: application compiles and all tests pass; models are idiomatic Kotlin

---

## Phase 9 — Owner Email Feature

First feature from TODO.md. Built on the new Kotlin models + Spring Data JDBC foundation.

- [ ] Add `email` field to `Owner` entity and schema migration (H2, PostgreSQL)
- [ ] Add REST API endpoint support (DTO, controller, validation)
- [ ] Update Thymeleaf owner forms and list views to show email
- [ ] Add tests (unit, integration, contract, mutation)
- [ ] Verify: owner can be created/edited with an email via both API and UI

---

## Phase 10 — Kotlin Migration: Repositories & Services

Migrate repository and service layers to Kotlin.

- [ ] Migrate repository implementations to Kotlin (using Spring Data JDBC idioms)
- [ ] Migrate service classes to Kotlin
- [ ] Verify: all tests pass; services and repos are idiomatic Kotlin

---

## Phase 11 — Feedback Form Feature

Second feature from TODO.md. Exercises the full Kotlin + JDBC stack.

- [ ] Define `Feedback` entity (Kotlin data class: name, email, message, createdAt) and schema migration (H2, PostgreSQL)
- [ ] Add repository, service, and REST API endpoint (`POST /api/v1/feedback`)
- [ ] Add Thymeleaf form for submitting feedback
- [ ] Add validation (non-blank, email format, message length)
- [ ] Add tests (unit, integration, contract, mutation)
- [ ] Verify: feedback can be submitted via API and UI; stored in DB; appears in admin view

---

## Phase 12 — Kotlin Migration: Controllers & Configuration

Migrate controllers and configuration to Kotlin; complete the language migration.

- [ ] Migrate REST controllers and WebMvc controllers to Kotlin
- [ ] Migrate Spring configuration classes to Kotlin
- [ ] Remove all Java source files; project is 100% Kotlin
- [ ] Verify: application compiles and runs as pure Kotlin; no `.java` source files remain

---

## Phase 13 — Vue.js Frontend (Initial Scaffold)

Introduce Vue.js to replace Thymeleaf views incrementally.

- [ ] Scaffold Vue.js app in `frontend/` with Vite
- [ ] Configure proxy to Spring Boot backend during development
- [ ] Replace the Welcome / home page Thymeleaf view with a Vue.js page
- [ ] Wire up Vue Router for SPA navigation
- [ ] Verify: home page renders from Vue; all other pages still served by Thymeleaf

---

## Phase 14 — Vue.js Owners Pages

Migrate the Owners domain from Thymeleaf to Vue.js.

- [ ] Vue.js Owners list page (replaces `/owners` Thymeleaf view)
- [ ] Vue.js Owner detail page (show owner info, pets, visits)
- [ ] Vue.js Owner create/edit forms
- [ ] Remove or redirect Thymeleaf `/owners` routes
- [ ] Verify: full Owners CRUD works via Vue.js SPA; API serves owner data

---

## Phase 15 — Vue.js Vets Page

Migrate the Vets domain from Thymeleaf to Vue.js.

- [ ] Vue.js Vets list page (replaces `/vets` Thymeleaf view)
- [ ] Remove or redirect Thymeleaf `/vets` route
- [ ] Verify: vets list renders from Vue.js; API serves vet data

---

## Phase 16 — Vue.js Pet and Visit Pages

Migrate Pet and Visit management from Thymeleaf to Vue.js.

- [ ] Vue.js Pet detail and edit pages
- [ ] Vue.js Visit add/list pages
- [ ] Remove or redirect Thymeleaf pet and visit routes
- [ ] Verify: pets and visits are fully manageable via Vue.js SPA

---

## Phase 17 — Vue.js Feedback Page

Migrate the Feedback form from Thymeleaf to Vue.js.

- [ ] Vue.js Feedback form page (replaces Thymeleaf feedback view)
- [ ] Remove or redirect Thymeleaf `/feedback` route
- [ ] Verify: feedback submitted via Vue.js form; stored in DB

---

## Phase 18 — Remove Thymeleaf

Once all views have been migrated, clean up the server-rendered layer.

- [ ] Remove Thymeleaf starter dependency and related templates
- [ ] Remove `WebMvc` controllers that only served Thymeleaf views
- [ ] Configure Spring Boot to serve `frontend/` static assets and forward SPA routes
- [ ] Verify: full app usable via Vue.js SPA only; no Thymeleaf artifacts remain
