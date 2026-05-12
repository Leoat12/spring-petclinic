# Plan — REST API Layer

## 1. Infrastructure & Dependencies

- [ ] Add MapStruct dependency and configure `maven-compiler-plugin` annotation processor
- [ ] Create `ApiError` DTO class for consistent error responses
- [ ] Create `GlobalExceptionHandler` `@RestControllerAdvice` for handling validation and not-found errors
- [ ] Add `spring-boot-starter-web` if not already present (required for `@RestController`)

## 2. DTOs & Mappers

- [ ] Create `OwnerDto` (and `OwnerCreateDto`, `OwnerUpdateDto` if differing fields)
- [ ] Create `PetDto` (and `PetCreateDto`, `PetUpdateDto`)
- [ ] Create `VisitDto` (and `VisitCreateDto`)
- [ ] Create `VetDto` with specialty information
- [ ] Create `SpecialtyDto`
- [ ] Add Bean Validation annotations to all DTOs
- [ ] Create MapStruct mapper interfaces: `OwnerMapper`, `PetMapper`, `VisitMapper`, `VetMapper`, `SpecialtyMapper`
- [ ] Create `PagedResultDto<T>` wrapper for paginated list responses

## 3. REST Controllers

- [ ] Create `OwnerRestController` — CRUD: `GET /api/v1/owners`, `GET /api/v1/owners/{id}`, `POST /api/v1/owners`, `PUT /api/v1/owners/{id}`, `DELETE /api/v1/owners/{id}`
- [ ] Create `PetRestController` — CRUD: `GET /api/v1/owners/{ownerId}/pets`, `POST /api/v1/owners/{ownerId}/pets`, `PUT /api/v1/owners/{ownerId}/pets/{petId}`, `DELETE /api/v1/owners/{ownerId}/pets/{petId}`
- [ ] Create `VisitRestController` — CRUD: `GET /api/v1/owners/{ownerId}/pets/{petId}/visits`, `POST /api/v1/owners/{ownerId}/pets/{petId}/visits`
- [ ] Create `VetRestController` — `GET /api/v1/vets`, `GET /api/v1/vets/{id}` (read-only for vets initially, matching existing UI behavior)
- [ ] Set proper HTTP status codes on all endpoints (201 for creation, 204 for deletion, 404 for not found, 400 for validation)
- [ ] Add pagination parameters to list endpoints (`page`, `size`, `sort`)

## 4. Integration Tests

- [ ] Add `OwnerRestControllerTests` — test all CRUD operations, validation errors, 404, pagination
- [ ] Add `PetRestControllerTests` — test CRUD under owner context, validation, 404
- [ ] Add `VisitRestControllerTests` — test creation and listing under pet context, validation
- [ ] Add `VetRestControllerTests` — test listing and individual retrieval
- [ ] Use `@SpringBootTest` with `MockMvc` or `WebTestClient` for all tests

## 5. Verification

- [ ] Run full test suite and confirm all existing Thymeleaf tests still pass
- [ ] Run new REST API tests and confirm all pass
- [ ] Manually verify: `curl /api/v1/owners` returns JSON list
- [ ] Manually verify: existing Thymeleaf UI still works unchanged