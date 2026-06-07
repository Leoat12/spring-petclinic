# Requirements — Kotlin Migration: Controllers & Configuration

## Scope

This phase migrates all remaining Java source files in `src/main/java` to Kotlin, making the project 100% Kotlin. The following categories are in scope:

**In scope:**
- REST controllers (`OwnerRestController`, `PetRestController`, `VisitRestController`, `VetRestController`, `FeedbackRestController`)
- WebMvc controllers (`OwnerController`, `PetController`, `VisitController`, `VetController`, `FeedbackController`, `WelcomeController`, `CrashController`)
- Spring configuration classes (`CacheConfiguration`, `WebConfiguration`, `PetClinicRuntimeHints`)
- DTOs (`OwnerDto`, `OwnerCreateDto`, `OwnerUpdateDto`, `PetDto`, `PetCreateDto`, `PetTypeDto`, `VisitDto`, `VisitCreateDto`, `VetDto`, `SpecialtyDto`, `FeedbackDto`, `FeedbackCreateDto`, `PagedResultDto`)
- Mappers (`OwnerMapper`, `PetMapper`, `PetTypeMapper`, `VisitMapper`, `VetMapper`, `SpecialtyMapper`, `FeedbackMapper`)
- Exception handling (`GlobalExceptionHandler`, `ResourceNotFoundException`, `ApiError`)
- Utilities (`PetTypeFormatter`, `PetValidator`, `PetClinicApplication`)
- Test classes in `src/test/java/`
- Removal of all `.java` source files

**Out of scope:**
- No new features or API endpoints
- No changes to HTML templates or CSS
- No changes to database schemas or migrations

## Decisions

- **Full Kotlin migration**: All Java source files will be converted and then deleted. No Java shims or interop bridges will be kept.
- **Spring Kotlin DSL**: Configuration classes will use the Kotlin functional bean registration DSL (`beans {}` / `bean {}`) where idiomatic, replacing `@Bean` methods in `@Configuration` classes where appropriate.
- **Idiomatic Kotlin**: DTOs will be converted to Kotlin data classes. Controllers will use Kotlin idioms (e.g., `lateinit` for injected dependencies, extension functions where helpful).
- **Single-pass migration**: All files are migrated together rather than incrementally, since the goal is to remove Java entirely in one step.

## Context

- Phases 8, 9, 10, and 11 already migrated models, repositories, and services to Kotlin. The existing Kotlin files in `src/main/kotlin/` are the reference for style and conventions.
- The project uses Spring Data JDBC (not JPA), so controllers inject repository interfaces directly (no `@Transactional` on controllers in the current architecture — that was removed in prior phases).
- Thymeleaf views are still in use and must continue to work. WebMvc controllers render Thymeleaf templates and must not change routing or view names.
- The `PetTypeFormatter` is registered in `WebConfiguration` and is required for Thymeleaf form binding to `PetType` objects.
- The `PetValidator` validates pet data in the `PetController` form flow.
- The `GlobalExceptionHandler` returns `ApiError` DTOs with `@RestControllerAdvice`.
- Test classes must also be migrated to Kotlin (or rewritten in Kotlin) since no `.java` files should remain.
- Build tool is Gradle (Kotlin DSL); the `build.gradle.kts` already has Kotlin plugin support from Phase 8.