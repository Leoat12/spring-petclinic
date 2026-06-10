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

- [x] Add `email` field to `Owner` entity and schema migration (H2, PostgreSQL)
- [x] Add REST API endpoint support (DTO, controller, validation)
- [x] Update Thymeleaf owner forms and list views to show email
- [x] Add tests (unit, integration, contract, mutation)
- [x] Verify: owner can be created/edited with an email via both API and UI

---

## Phase 10 — Kotlin Migration: Repositories & Services

Migrate repository and service layers to Kotlin.

- [x] Migrate repository implementations to Kotlin (using Spring Data JDBC idioms)
- [x] Migrate service classes to Kotlin
- [x] Verify: all tests pass; services and repos are idiomatic Kotlin

---

## Phase 11 — Feedback Form Feature

Second feature from TODO.md. Built on the new Kotlin + Spring Data JDBC foundation.

- [x] Define `Feedback` entity (Kotlin data class: name, email, message, createdAt) and schema migration (H2, PostgreSQL)
- [x] Add repository, service, and REST API endpoint (`POST /api/v1/feedback`)
- [x] Add Thymeleaf form for submitting feedback
- [x] Add validation (non-blank, email format, message length)
- [x] Add tests (unit, integration, contract, mutation)
- [x] Verify: feedback can be submitted via API and UI; stored in DB; appears in admin view

---

## Phase 12 — Kotlin Migration: Controllers & Configuration

Migrate controllers and configuration to Kotlin; complete the language migration.

- [x] Migrate REST controllers and WebMvc controllers to Kotlin
- [x] Migrate Spring configuration classes to Kotlin
- [x] Remove all Java source files; project is 100% Kotlin
- [x] Verify: application compiles and runs as pure Kotlin; no `.java` source files remain

---

## Phase 13 — Vue.js Frontend (Initial Scaffold)

Introduce Vue.js to replace Thymeleaf views incrementally.

- [x] Scaffold Vue.js app in `frontend/` with Vite
- [x] Configure proxy to Spring Boot backend during development
- [x] Replace the Welcome / home page Thymeleaf view with a Vue.js page
- [x] Wire up Vue Router for SPA navigation
- [x] Verify: home page renders from Vue; all other pages still served by Thymeleaf

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

---

## Phase 19 — Service Layer Introduction

Introduce a Kotlin service layer between controllers and repositories to encapsulate business logic, transaction boundaries, and validation.

- [ ] Create `OwnerService`, `VetService`, `PetService`, `VisitService`, `FeedbackService` in Kotlin
- [ ] Move validation logic and multi-repository operations out of controllers into services
- [ ] Controllers depend on services, not repositories
- [ ] Move `@Transactional` annotations from controllers to service methods
- [ ] Update all controller and test wiring
- [ ] Verify: all existing tests pass; controllers no longer inject repositories directly

---

## Phase 20 — Owner Search by Email

Add the ability to search owners by email via the REST API.

- [ ] Add `GET /api/v1/owners?email={email}` — exact match query parameter
- [ ] Add `GET /api/v1/owners?emailContains={substring}` — partial match query parameter
- [ ] Add Kotlin repository methods using `JdbcClient`
- [ ] Update the owner list view to optionally filter by email
- [ ] Add integration and contract tests
- [ ] Verify: owners can be searched by exact or partial email match via the API

---

## Phase 21 — API Versioning Strategy

Document and formalize the API versioning convention before the Vue.js frontend depends heavily on the API.

- [ ] Document versioning convention (URL-path: `/api/v1/`) in `API_CONVENTIONS.md`
- [ ] Decide and document: URL-path versioning as the standard approach
- [ ] Define guidelines for when and how to introduce `/api/v2`
- [ ] Add deprecation headers to existing endpoints when `v2` is introduced in the future
- [ ] Verify: API conventions document reviewed and versioning strategy is clear

---

## Phase 22 — Vet CRUD Endpoints

Complete the REST API for vets and specialties by adding write endpoints.

- [ ] Add `POST /api/v1/vets` — create a vet
- [ ] Add `PUT /api/v1/vets/{id}` — update a vet
- [ ] Add `DELETE /api/v1/vets/{id}` — delete a vet
- [ ] Add `POST /api/v1/specialties` — create a specialty
- [ ] Add `PUT /api/v1/specialties/{id}` — update a specialty
- [ ] Add corresponding Kotlin service layer methods and repository queries
- [ ] Add integration and contract tests for all new endpoints
- [ ] Verify: vets and specialties can be created, updated, and deleted via the API

---

## Phase 23 — Feedback Update & Delete

Add update and delete operations for the feedback resource (admin-level endpoints).

- [ ] Add `PUT /api/v1/feedback/{id}` — update feedback
- [ ] Add `DELETE /api/v1/feedback/{id}` — delete feedback
- [ ] Add confirmation UI for delete in the admin view
- [ ] Add integration and contract tests for new endpoints
- [ ] Note: admin role gating will be added in Phase 24 (Authentication & Authorization)
- [ ] Verify: feedback can be updated and deleted via the API

---

## Phase 24 — Authentication & Authorization

Introduce Spring Security with JWT-based authentication and role-based access control. Implemented after Vue.js replaces Thymeleaf to avoid building two auth systems.

- [ ] Add Spring Security with JWT authentication (API-first, no server-rendered login)
- [ ] Define roles: `ADMIN`, `VET`, `OWNER`
- [ ] Secure write endpoints (`POST`, `PUT`, `DELETE`) behind appropriate roles
- [ ] Leave read endpoints publicly accessible (list owners, vets, etc.)
- [ ] Add Vue.js login page and client-side route guards
- [ ] Add token refresh and logout endpoints
- [ ] Gate Phase 23 feedback admin endpoints behind `ADMIN` role
- [ ] Add integration and contract tests for secured endpoints
- [ ] Verify: unauthenticated requests to write endpoints return 401; role-gated endpoints return 403

---

## Phase 25 — Kubernetes Deployment Manifests

Add Kubernetes manifests for deploying PetClinic to a local Kubernetes cluster, aligning with the project mission of teaching developer workflows and CI/CD.

- [ ] Create `k8s/` directory with `deployment.yaml`, `service.yaml`, `configmap.yaml`
- [ ] Add a PostgreSQL StatefulSet or reference an external database
- [ ] Add `k8s/README.md` with `kubectl apply` instructions
- [ ] Optionally add a Helm chart under `helm/petclinic/`
- [ ] Verify: `kind` or `minikube` can run the full stack locally
