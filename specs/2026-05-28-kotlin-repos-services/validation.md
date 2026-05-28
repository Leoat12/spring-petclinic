# Validation — Kotlin Migration: Repositories & Services

## Acceptance Criteria

- [ ] All repository interfaces (`OwnerRepository`, `PetTypeRepository`, `VetRepository`, `SpecialtyRepository`) are Kotlin source files (`.kt`)
- [ ] All repository implementations (`JdbcClientOwnerRepository`, `JdbcClientPetTypeRepository`, `JdbcClientVetRepository`, `JdbcClientSpecialtyRepository`) are Kotlin source files (`.kt`)
- [ ] No `.java` files remain in any repository-related package under `src/main/java`
- [ ] `./gradlew build` passes with no compilation errors and all tests green
- [ ] `./gradlew pitest` passes with mutation score at or above the 80% threshold

## Manual Checks

```bash
./gradlew build
./gradlew pitest
```

Verify no Java repository files remain:
```bash
find src/main/java -name "*Repository*.java" | grep -v test
```
This should return no results.

Verify Kotlin repository files exist:
```bash
find src/main/kotlin -name "*Repository*.kt"
```
This should list all four implementation files and four interface files.

Run the application locally and exercise owner CRUD, vet listing, and pet type operations:
- `./gradlew bootRun` (with H2 dev profile)
- Create, find, edit, and delete an owner via the Thymeleaf UI
- Verify vet list page loads correctly
- Verify REST API endpoints (`/api/v1/owners`, `/api/v1/vets`, `/api/v1/petTypes`) return correct data

## Regression Concerns

- Owner aggregate cascade operations (saving pets and visits alongside owners) must continue to work correctly
- Pagination on owner list queries must behave identically to the Java implementation
- Specialty loading for vets must produce the same results
- All existing integration and contract tests for REST API endpoints must pass
- Thymeleaf UI must remain fully functional