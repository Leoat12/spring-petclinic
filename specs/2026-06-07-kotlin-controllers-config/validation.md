# Validation — Kotlin Migration: Controllers & Configuration

## Acceptance Criteria

- [ ] No `.java` source files remain in `src/main/java/` or `src/test/java/`
- [ ] `./gradlew build` succeeds with no compilation errors
- [ ] All existing unit tests pass (`./gradlew test`)
- [ ] All integration tests pass (including Testcontainers-based PostgreSQL tests)
- [ ] All contract tests pass
- [ ] Mutation testing score is maintained at ≥ 80% (`./gradlew pitest`)
- [ ] The application starts successfully via `./gradlew bootRun`

## Manual Checks

### REST API Endpoints

```bash
# Start the app with dev profile
./gradlew bootRun

# Verify Owner endpoints
curl -s http://localhost:8080/api/v1/owners | head -c 200
curl -s http://localhost:8080/api/v1/owners/1 | head -c 200

# Verify Pet endpoints
curl -s http://localhost:8080/api/v1/pets | head -c 200
curl -s http://localhost:8080/api/v1/pettypes | head -c 200

# Verify Vet endpoints
curl -s http://localhost:8080/api/v1/vets | head -c 200

# Verify Visit endpoints
curl -s http://localhost:8080/api/v1/visits?petId=1 | head -c 200

# Verify Feedback endpoint
curl -s -X POST http://localhost:8080/api/v1/feedback \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","email":"test@example.com","message":"Hello"}'
```

### Thymeleaf UI

- Visit `http://localhost:8080/` — home page renders
- Visit `http://localhost:8080/owners` — owner list renders
- Visit `http://localhost:8080/owners/1` — owner detail with pets and visits
- Visit `http://localhost:8080/vets.html` — vets list renders
- Visit `http://localhost:8080/feedback` — feedback form renders
- Create/edit an owner — form submission works
- Add a pet to an owner — form submission works
- Add a visit to a pet — form submission works

## Regression Concerns

- **Thymeleaf form binding**: The `PetTypeFormatter` must remain registered correctly so that `<select>` elements for pet types bind properly in forms
- **PetValidator**: Custom validation on pet forms must continue to work
- **Error handling**: The `GlobalExceptionHandler` must continue to return proper JSON error responses with correct HTTP status codes
- **Cache configuration**: `CacheConfiguration` must continue to configure Caffeine caches for vets
- **Runtime hints**: `PetClinicRuntimeHints` must continue to register reflection hints for AOT compatibility
- **Docker build**: Container build must still work (`docker build .`)