# Requirements — REST API Layer

## Scope

Expose the existing domain (Owners, Pets, Visits, Vets, Specialties) as JSON REST resources under `/api/v1`. All four domains are covered equally with full CRUD where applicable.

**In scope:**
- `@RestController` classes for Owner, Pet, Visit, Vet (and Specialty via Vet)
- DTOs for each domain, kept in a separate `dto` package from entities
- MapStruct mappers for entity-to-DTO and DTO-to-entity conversion
- Proper HTTP status codes (201 Created, 204 No Content, 404 Not Found, 400 Bad Request)
- Error response body using a consistent `ApiError` structure
- Pagination for list endpoints (Owners, Vets, Visits)
- Integration tests for every endpoint
- `/api/v1` URL prefix for all endpoints

**Out of scope:**
- HATEOAS / hypermedia
- API versioning beyond the `/api/v1` prefix
- Authentication / authorization
- Thymeleaf UI changes (existing UI must remain functional but is not the focus of verification)
- Spring Data REST auto-generation

## Decisions

- **Manual `@RestController` classes** — hand-written controllers rather than Spring Data REST, giving full control over endpoint design and error handling.
- **MapStruct for DTO mapping** — compile-time generated mappers reduce boilerplate and avoid reflection. MapStruct will be added as a dependency with the annotation processor.
- **DTOs in a separate package** — entities live in `org.springframework.samples.petclinic.model`; DTOs will live in `org.springframework.samples.petclinic.rest.dto`. This keeps the API contract decoupled from the persistence model.
- **Layer-by-layer implementation** — all DTOs and mappers first, then all controllers, then all tests. This avoids partial states where a controller references a DTO that doesn't exist yet.
- **Existing `@Controller` classes untouched** — new `@RestController` classes sit alongside existing Thymeleaf controllers; no modification to existing web layer.

## Context

- The project uses Spring Boot 4.0.3 with Spring Data JPA. Each domain (Owner, Pet, Visit, Vet, Specialty) has a corresponding entity and repository.
- The existing `@Controller` classes serve Thymeleaf templates. They must continue to work alongside the new REST controllers.
- Bean Validation (Hibernate Validator) is already on the classpath and used in entity and form validation. DTOs should also use Bean Validation annotations.
- H2 is the default dev database; MySQL and PostgreSQL profiles exist. Integration tests should use the default H2 profile.
- Spring Boot Actuator is already configured; new REST endpoints should not conflict with Actuator paths.
- The build uses Maven (primary). MapStruct requires the `maven-compiler-plugin` annotation processor configuration.