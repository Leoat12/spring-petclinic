# Validation — Feedback Form Feature

## Acceptance Criteria

- [ ] `Feedback` Kotlin data class exists with id, name, email, message, and createdAt fields
- [ ] Flyway migration creates the `feedback` table in both H2 and PostgreSQL
- [ ] `POST /api/v1/feedback` returns 201 Created with valid payload
- [ ] `POST /api/v1/feedback` returns 400 Bad Request for invalid payloads (blank name, invalid email, empty message, message too long)
- [ ] `GET /api/v1/feedback` returns 200 OK with list of feedback entries
- [ ] Thymeleaf feedback form submits feedback and shows confirmation
- [ ] Thymeleaf admin list page displays all submitted feedback
- [ ] All automated tests pass: unit, integration, contract, mutation

## Manual Checks

```bash
# Start the app with dev profile
./gradlew bootRun

# Submit feedback via API
curl -X POST http://localhost:8080/api/v1/feedback \
  -H "Content-Type: application/json" \
  -d '{"name":"Test User","email":"test@example.com","message":"Great clinic!"}'

# List feedback via API
curl http://localhost:8080/api/v1/feedback

# Visit feedback form in browser
open http://localhost:8080/feedback

# Visit admin feedback list in browser
open http://localhost:8080/admin/feedback
```

## Regression Concerns

- Owner, Pet, Visit, and Vet CRUD operations must continue working unchanged via both API and Thymeleaf UI
- Existing REST API contract tests for all other endpoints must pass
- Pitest mutation score must remain ≥ 80%
- The new `feedback` table must not conflict with existing schema objects