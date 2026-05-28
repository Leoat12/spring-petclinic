# Plan — Kotlin Migration: Repositories & Services

## 1. Migrate Repository Interfaces

- [ ] Migrate `OwnerRepository` interface to Kotlin (`OwnerRepository.kt`)
- [ ] Migrate `PetTypeRepository` interface to Kotlin (`PetTypeRepository.kt`)
- [ ] Migrate `VetRepository` interface to Kotlin (`VetRepository.kt`)
- [ ] Migrate `SpecialtyRepository` interface to Kotlin (`SpecialtyRepository.kt`)
- [ ] Verify: project compiles with Kotlin interfaces; Java implementation classes still wired correctly

## 2. Migrate Repository Implementations — Simple Repositories

- [ ] Migrate `JdbcClientPetTypeRepository` to Kotlin
- [ ] Migrate `JdbcClientSpecialtyRepository` to Kotlin
- [ ] Delete corresponding Java implementation files
- [ ] Verify: pet type and specialty operations work (integration tests pass)

## 3. Migrate Repository Implementations — Vet Repository

- [ ] Migrate `JdbcClientVetRepository` to Kotlin (includes specialty-loading logic)
- [ ] Delete the Java `JdbcClientVetRepository.java` file
- [ ] Verify: vet queries with specialties work (integration tests pass)

## 4. Migrate Repository Implementations — Owner Repository

- [ ] Migrate `JdbcClientOwnerRepository` to Kotlin (the most complex repo — owns Pet and Visit aggregation)
- [ ] Delete the Java `JdbcClientOwnerRepository.java` file
- [ ] Verify: owner CRUD with pet/visit cascading works (integration tests pass)

## 5. Cleanup and Final Verification

- [ ] Remove any remaining `.java` repository files from `src/main/java`
- [ ] Ensure no Java source files reference deleted repository classes
- [ ] Run `./gradlew build` — all tests pass
- [ ] Run `./gradlew pitest` — mutation score meets threshold
- [ ] Verify no `.java` repository or service files remain in the codebase