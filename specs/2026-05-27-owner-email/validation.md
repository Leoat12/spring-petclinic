# Validation — Owner Email Feature

## Acceptance Criteria

- [ ] Owner can be created with an email address via the REST API (`POST /api/v1/owners`)
- [ ] Owner can be created without an email address via the REST API (field omitted or null)
- [ ] Owner can be edited to add, change, or remove an email address via the REST API (`PUT /api/v1/owners/{id}`)
- [ ] Email appears in owner detail and list API responses when present; absent or null when not set
- [ ] Submitting an invalidly formatted email via the API returns a 400 with a validation error
- [ ] Submitting a blank email via the API is accepted (treated as "no email")
- [ ] Owner can be created and edited with email via the Thymeleaf UI forms
- [ ] Email is displayed on the owner detail page when present; hidden/omitted when null or blank
- [ ] Email is shown in the owner list view for owners that have one
- [ ] Existing owners (seed data) without an email still load and display correctly
- [ ] H2 dev profile starts without errors and serves updated schema
- [ ] PostgreSQL + Testcontainers profile starts without errors and serves updated schema

## Manual Checks

- Start the app with `dev` profile: `./gradlew bootRun`
- Visit `http://localhost:8080/owners` — verify email column appears for owners that have one
- Visit an owner detail page — verify email is shown if present
- Create a new owner with a valid email via the UI — verify it persists
- Edit an owner to remove their email — verify it saves as blank/null
- Create an owner via `curl`:
  ```bash
  curl -X POST http://localhost:8080/api/v1/owners \
    -H 'Content-Type: application/json' \
    -d '{"firstName":"Test","lastName":"User","address":"123 St","city":"Springfield","telephone":"5551234","email":"test@example.com"}'
  ```
- Create an owner without email:
  ```bash
  curl -X POST http://localhost:8080/api/v1/owners \
    -H 'Content-Type: application/json' \
    -d '{"firstName":"No","lastName":"Email","address":"456 Ave","city":"Shelbyville","telephone":"5559999"}'
  ```
- Submit an invalid email and verify 400 response:
  ```bash
  curl -X POST http://localhost:8080/api/v1/owners \
    -H 'Content-Type: application/json' \
    -d '{"firstName":"Bad","lastName":"Email","address":"789 Blvd","city":"Capital","telephone":"5550000","email":"not-an-email"}'
  ```
- Run `./gradlew pitest` and verify mutation score ≥ 80%

## Regression Concerns

- Existing owner CRUD via REST API must continue to work (all existing tests pass)
- Existing Thymeleaf owner pages must continue to work (list, detail, create, edit)
- Existing Pet and Visit pages must be unaffected
- Existing Vet and Specialty functionality must be unaffected
- Application must start successfully with both H2 and PostgreSQL profiles
- Pitest mutation score must not drop below the 80% threshold