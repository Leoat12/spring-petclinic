# Plan — Owner Email Feature

## 1. Schema Migration

- [ ] Add `email` column (VARCHAR, nullable) to `owners` table in H2 DDL script
- [ ] Add `email` column (VARCHAR, nullable) to `owners` table in PostgreSQL DDL script
- [ ] Add ALTER TABLE migration script for PostgreSQL (to update existing databases)
- [ ] Update H2 seed data to include email values for some owners (leave others without email)

## 2. Backend — Entity & Repository

- [ ] Add `email` property to `Owner` Kotlin data class
- [ ] Verify Spring Data JDBC maps the new column correctly (no annotation needed if column name matches)
- [ ] Update any existing repository SQL statements that explicitly list columns to include `email`

## 3. Backend — DTO, Service & Controller

- [ ] Add `email` field to Owner DTO (nullable, with `@Email` and `@Nullable` validation annotations)
- [ ] Update DTO ↔ entity mapping logic to handle the `email` field
- [ ] Update Owner REST controller to accept/return `email` in create and update payloads
- [ ] Add `email` to owner detail and list response representations

## 4. UI — Thymeleaf Views

- [ ] Add email input field to the owner creation form
- [ ] Add email input field to the owner edit form
- [ ] Display email on the owner detail page (conditionally, only when present)
- [ ] Display email in the owner list view
- [ ] Ensure form validation messages for invalid email format render correctly

## 5. Tests

- [ ] Add unit tests for Owner DTO validation (valid email, invalid email, null, blank)
- [ ] Add integration tests for Owner REST API endpoints with email (create, update, list, detail)
- [ ] Add contract tests for Owner API with email field
- [ ] Add tests verifying backward compatibility: owners without email continue to work
- [ ] Add Thymeleaf view integration tests verifying email renders in forms and detail pages
- [ ] Run Pitest and ensure mutation score remains at or above 80%