# Validation — REST API Layer

## Acceptance Criteria

- [ ] `GET /api/v1/owners` returns a paginated JSON list of owners with 200 OK
- [ ] `POST /api/v1/owners` creates an owner and returns 201 Created with location header
- [ ] `GET /api/v1/owners/{id}` returns a single owner JSON with 200 OK, or 404 if not found
- [ ] `PUT /api/v1/owners/{id}` updates an owner and returns 200 OK
- [ ] `DELETE /api/v1/owners/{id}` deletes an owner and returns 204 No Content
- [ ] `GET /api/v1/owners/{ownerId}/pets` returns pets for an owner
- [ ] `POST /api/v1/owners/{ownerId}/pets` creates a pet under an owner with 201 Created
- [ ] `GET /api/v1/owners/{ownerId}/pets/{petId}/visits` returns visits for a pet
- [ ] `POST /api/v1/owners/{ownerId}/pets/{petId}/visits` creates a visit with 201 Created
- [ ] `GET /api/v1/vets` returns a JSON list of vets with their specialties
- [ ] Validation errors return 400 with a structured `ApiError` JSON body
- [ ] All endpoints return JSON content type (`application/json`)

## Manual Checks

```bash
# Build the project
./mvnw clean verify

# Start the application
./mvnw spring-boot:run

# Test owner list
curl -s http://localhost:8080/api/v1/owners | jq .

# Test owner creation
curl -s -X POST http://localhost:8080/api/v1/owners \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Test","lastName":"User","address":"1 Main St","city":"Town","telephone":"1234567890"}' | jq .

# Test vet list
curl -s http://localhost:8080/api/v1/vets | jq .

# Test 404
curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/v1/owners/99999

# Test validation error
curl -s -X POST http://localhost:8080/api/v1/owners \
  -H "Content-Type: application/json" \
  -d '{"firstName":"","lastName":""}' | jq .
```

## Regression Concerns

- Existing Thymeleaf `@Controller` routes (`/owners`, `/vets`, etc.) must continue to serve HTML pages unchanged
- Existing Thymeleaf integration tests must continue to pass
- No changes to entity classes or repository interfaces should break existing service-layer behavior
- H2 and PostgreSQL database profiles must both still work