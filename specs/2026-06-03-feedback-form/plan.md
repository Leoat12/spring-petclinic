# Plan — Feedback Form Feature

## 1. Entity & Schema

- [ ] Create `Feedback` Kotlin data class with fields: id, name, email, message, createdAt
- [ ] Add `@Table` and `@Id` annotations following Spring Data JDBC conventions
- [ ] Add Bean Validation annotations (non-blank name, email format, message length)
- [ ] Create Flyway migration for `feedback` table (compatible with H2 and PostgreSQL)
- [ ] Add seed data for H2 dev profile

## 2. Repository & Service

- [ ] Create `FeedbackRepository` using Spring Data JDBC `CrudRepository`
- [ ] Create `FeedbackService` in Kotlin with submit and list operations
- [ ] Add unit tests for `FeedbackService`

## 3. REST API

- [ ] Create `FeedbackDto` (request and response) with validation annotations
- [ ] Create `FeedbackRestController` with `POST /api/v1/feedback` endpoint
- [ ] Add `GET /api/v1/feedback` endpoint for admin listing
- [ ] Add proper HTTP status codes (201 Created, 200 OK, 400 Bad Request)
- [ ] Add integration tests for both endpoints
- [ ] Add contract tests for both endpoints

## 4. Thymeleaf UI

- [ ] Create feedback form Thymeleaf view (`feedback/form.html`)
- [ ] Create feedback admin list Thymeleaf view (`feedback/list.html`)
- [ ] Add `FeedbackController` (WebMvc) to serve feedback form and handle submission
- [ ] Add admin controller to list all feedback entries
- [ ] Add navigation link to feedback form from the home page

## 5. Tests & Coverage

- [ ] Add unit tests for `FeedbackService` validation logic
- [ ] Add integration tests for repository layer
- [ ] Add contract tests for REST API endpoints
- [ ] Add Testcontainers-based integration test for PostgreSQL
- [ ] Verify Pitest mutation score stays ≥ 80% with new code
- [ ] Add `.http` entries for feedback endpoints in `petclinic.http`