# Roadmap

Small phases, each deliverable and testable on its own.

---

## Phase 0 — Project Infrastructure

Set up containerization and CI so every subsequent phase has automated verification.

- [ ] Add a multi-stage `Dockerfile` for reproducible container builds
- [ ] Add `.dockerignore`
- [ ] Add GitHub Actions workflow: build, test, and publish container image
- [ ] Verify: pushing to `main` triggers a green CI run

---

## Phase 1 — REST API Layer

Expose the existing domain (Owners, Pets, Visits, Vets, Specialties) as JSON resources. API-first: no UI changes yet.

- [ ] Add `@RestController` classes alongside existing `@Controller` classes for each domain (Owner, Pet, Visit, Vet)
- [ ] Use Spring Data JPA projections/DTOs to avoid leaking internal entities
- [ ] Add proper HTTP status codes, error responses, and pagination where appropriate
- [ ] Add integration tests for every endpoint
- [ ] Verify: `curl /api/v1/owners` returns JSON; existing Thymeleaf UI still works unchanged

---

## Phase 2 — Testing Improvements

Harden the test suite before adding new features.

- [ ] Expand integration tests for all repository and service layers
- [ ] Add Testcontainers-based integration test profiles for MySQL and PostgreSQL
- [ ] Configure Pitest (mutation testing) and add to CI
- [ ] Add contract tests for the new REST API endpoints
- [ ] Verify: mutation coverage threshold enforced; contract tests pass in CI

---

## Phase 3 — Owner Email Feature

First feature from TODO.md. Simple data model extension — good candidate to exercise the new API-first workflow.

- [ ] Add `email` field to `Owner` entity and schema migrations (H2, MySQL, PostgreSQL)
- [ ] Add REST API endpoint support (DTO, controller, validation)
- [ ] Update Thymeleaf owner forms and list views to show email
- [ ] Add tests (unit, integration, contract, mutation)
- [ ] Verify: owner can be created/edited with an email via both API and UI

---

## Phase 4 — Feedback Form Feature

Second feature from TODO.md. Adds a new domain concept — exercises the full stack from entity to API to UI.

- [ ] Define `Feedback` entity (name, email, message, createdAt)
- [ ] Add repository, service, and REST API endpoint (`POST /api/v1/feedback`)
- [ ] Add Thymeleaf form for submitting feedback
- [ ] Add validation (non-blank, email format, message length)
- [ ] Add tests (unit, integration, contract, mutation)
- [ ] Verify: feedback can be submitted via API and UI; stored in DB; appears in admin view

---

## Phase 5 — Vue.js Frontend (Initial Scaffold)

Introduce Vue.js to replace Thymeleaf views incrementally.

- [ ] Scaffold Vue.js app in `frontend/` with Vite
- [ ] Configure proxy to Spring Boot backend during development
- [ ] Replace the Welcome / home page Thymeleaf view with a Vue.js page
- [ ] Wire up Vue Router for SPA navigation
- [ ] Verify: home page renders from Vue; all other pages still served by Thymeleaf

---

## Phase 6 — Vue.js Owners Pages

Migrate the Owners domain from Thymeleaf to Vue.js.

- [ ] Vue.js Owners list page (replaces `/owners` Thymeleaf view)
- [ ] Vue.js Owner detail page (show owner info, pets, visits)
- [ ] Vue.js Owner create/edit forms
- [ ] Remove or redirect Thymeleaf `/owners` routes
- [ ] Verify: full Owners CRUD works via Vue.js SPA; API serves owner data

---

## Phase 7 — Vue.js Vets Page

Migrate the Vets domain from Thymeleaf to Vue.js.

- [ ] Vue.js Vets list page (replaces `/vets` Thymeleaf view)
- [ ] Remove or redirect Thymeleaf `/vets` route
- [ ] Verify: vets list renders from Vue.js; API serves vet data

---

## Phase 8 — Vue.js Pet and Visit Pages

Migrate Pet and Visit management from Thymeleaf to Vue.js.

- [ ] Vue.js Pet detail and edit pages
- [ ] Vue.js Visit add/list pages
- [ ] Remove or redirect Thymeleaf pet and visit routes
- [ ] Verify: pets and visits are fully manageable via Vue.js SPA

---

## Phase 9 — Vue.js Feedback Page

Migrate the Feedback form from Thymeleaf to Vue.js.

- [ ] Vue.js Feedback form page (replaces Thymeleaf feedback view)
- [ ] Remove or redirect Thymeleaf `/feedback` route
- [ ] Verify: feedback submitted via Vue.js form; stored in DB

---

## Phase 10 — Remove Thymeleaf

Once all views have been migrated, clean up the server-rendered layer.

- [ ] Remove Thymeleaf starter dependency and related templates
- [ ] Remove `WebMvc` controllers that only served Thymeleaf views
- [ ] Configure Spring Boot to serve `frontend/` static assets and forward SPA routes
- [ ] Verify: full app usable via Vue.js SPA only; no Thymeleaf artifacts remain