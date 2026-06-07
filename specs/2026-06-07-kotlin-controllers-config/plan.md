# Plan — Kotlin Migration: Controllers & Configuration

## 1. REST Controllers & Supporting Classes

Migrate the REST API layer to Kotlin first, since it has no UI dependency and can be verified via curl and contract tests.

- [ ] Migrate `OwnerRestController` to Kotlin
- [ ] Migrate `PetRestController` to Kotlin
- [ ] Migrate `VisitRestController` to Kotlin
- [ ] Migrate `VetRestController` to Kotlin
- [ ] Migrate `FeedbackRestController` to Kotlin
- [ ] Migrate `GlobalExceptionHandler` to Kotlin
- [ ] Migrate `ResourceNotFoundException` to Kotlin
- [ ] Migrate `ApiError` to Kotlin
- [ ] Verify: all REST controller tests and contract tests pass

## 2. DTOs & Mappers

Migrate data transfer objects and their mappers, which are referenced by REST controllers.

- [ ] Migrate all DTOs (`OwnerDto`, `OwnerCreateDto`, `OwnerUpdateDto`, `PetDto`, `PetCreateDto`, `PetTypeDto`, `VisitDto`, `VisitCreateDto`, `VetDto`, `SpecialtyDto`, `FeedbackDto`, `FeedbackCreateDto`, `PagedResultDto`) to Kotlin data classes
- [ ] Migrate all mapper classes (`OwnerMapper`, `PetMapper`, `PetTypeMapper`, `VisitMapper`, `VetMapper`, `SpecialtyMapper`, `FeedbackMapper`) to Kotlin
- [ ] Verify: DTO serialization/deserialization works; mapper tests pass

## 3. WebMvc Controllers & Utilities

Migrate the server-rendered UI controllers and their supporting utility classes.

- [ ] Migrate `OwnerController` to Kotlin
- [ ] Migrate `PetController` to Kotlin
- [ ] Migrate `VisitController` to Kotlin
- [ ] Migrate `VetController` to Kotlin
- [ ] Migrate `FeedbackController` to Kotlin
- [ ] Migrate `WelcomeController` to Kotlin
- [ ] Migrate `CrashController` to Kotlin
- [ ] Migrate `PetTypeFormatter` to Kotlin
- [ ] Migrate `PetValidator` to Kotlin
- [ ] Verify: all WebMvc controller tests pass; Thymeleaf views render correctly

## 4. Configuration & Application

Migrate Spring configuration classes and the main application entry point.

- [ ] Migrate `CacheConfiguration` to Kotlin, converting `@Bean` methods to Kotlin DSL where idiomatic
- [ ] Migrate `WebConfiguration` to Kotlin, using Kotlin DSL for bean registration
- [ ] Migrate `PetClinicRuntimeHints` to Kotlin
- [ ] Migrate `PetClinicApplication` to Kotlin
- [ ] Verify: application context starts successfully; all beans are wired correctly

## 5. Test Classes

Migrate all test classes from Java to Kotlin so no `.java` files remain in `src/test/`.

- [ ] Migrate all test classes under `src/test/java/` to Kotlin equivalents in `src/test/kotlin/`
- [ ] Remove all `.java` test source files
- [ ] Verify: all tests pass (unit, integration, contract, mutation)

## 6. Cleanup

Remove all remaining Java source files and package-info files.

- [ ] Delete all `.java` files from `src/main/java/` and `src/test/java/`
- [ ] Remove `package-info.java` files (not needed in Kotlin)
- [ ] Remove empty `src/main/java/` and `src/test/java/` directory trees
- [ ] Update Gradle source sets if needed (remove `java` source set if it becomes empty)
- [ ] Verify: `./gradlew build` succeeds with zero `.java` files in the project; application starts and all endpoints respond correctly