# Plan — Kotlin Migration: Setup & Models

## 1. Gradle Kotlin Setup

- [ ] Add Kotlin plugin (`org.jetbrains.kotlin.jvm`) to `build.gradle`
- [ ] Add Kotlin standard library and `jackson-module-kotlin` dependencies
- [ ] Configure Kotlin compiler to target JVM 21
- [ ] Configure mixed Java/Kotlin source sets so both compile together
- [ ] Verify `./gradlew build` passes with Kotlin plugin in place (no model changes yet)

## 2. Migrate Model Classes to Kotlin

- [ ] Create `src/main/kotlin` source directory under the existing package structure
- [ ] Migrate `Specialty` to a Kotlin data class with `@Table` / `@Id` annotations
- [ ] Migrate `Vet` to a Kotlin data class (depends on Specialty)
- [ ] Migrate `Owner` to a Kotlin data class
- [ ] Migrate `Pet` to a Kotlin data class (depends on Owner / PetType)
- [ ] Migrate `Visit` to a Kotlin data class (depends on Pet)
- [ ] Migrate `PetType` to a Kotlin data class if it exists as a separate entity
- [ ] Remove the corresponding Java model files after each successful migration
- [ ] Update any Spring Data JDBC repository interfaces that reference the old Java class names (if needed)

## 3. Update Tests

- [ ] Update existing unit tests to work with Kotlin data classes (property access, constructor calls)
- [ ] Ensure integration tests pass with Kotlin models
- [ ] Add Kotlin-specific tests for data class behavior (copy, equality) if needed

## 4. Verify

- [ ] Run `./gradlew build` — all tests pass
- [ ] Run `./gradlew pitest` — mutation score ≥ 80%
- [ ] Start the application locally — Thymeleaf UI works unchanged
- [ ] Hit `curl /api/v1/owners` — REST API returns correct JSON
- [ ] Confirm no Java model source files remain for migrated entities