# Requirements — Kotlin Migration: Repositories & Services

## Scope

This phase migrates all remaining Java repository code to Kotlin. The project has no separate service layer — controllers call repositories directly — so this phase covers repositories only.

**In scope:**
- Migrate all repository interfaces to Kotlin (`OwnerRepository`, `PetTypeRepository`, `VetRepository`, `SpecialtyRepository`)
- Migrate all `JdbcClient`-based repository implementations to Kotlin (`JdbcClientOwnerRepository`, `JdbcClientPetTypeRepository`, `JdbcClientVetRepository`, `JdbcClientSpecialtyRepository`)
- Remove all `.java` repository files from `src/main/java`

**Out of scope:**
- Controllers and configuration (Phase 12)
- REST API controllers (remain Java for now)
- Thymeleaf WebMvc controllers (remain Java for now)
- Test classes (migrated alongside or after, as needed)
- Any behavioral changes or new features

## Decisions

- **Follow existing patterns**: Repository implementations will remain `JdbcClient`-based. No switch to `CrudRepository` interfaces or coroutines. The Kotlin versions will mirror the current Java structure, using the same `JdbcClient` and `JdbcTemplate` APIs.
- **Idiomatic Kotlin where safe**: Use Kotlin data class features where applicable, but preserve the current mutable-entity pattern (Kotlin models already use mutable properties from Phase 8). Use Kotlin string templates, `val`/`var` properly, and eliminate unnecessary null checks.
- **No service layer introduction**: The project currently has no dedicated service classes — controllers inject repositories directly. This phase does not introduce a service layer; that is a separate design decision.

## Context

- Kotlin models (`Owner`, `Pet`, `PetType`, `Visit`, `Vet`, `Specialty`, `Vets`, `Person`, `NamedEntity`, `BaseEntity`) were already migrated to Kotlin data classes in Phase 8, located under `src/main/kotlin/`.
- Repositories use Spring's `JdbcClient` API for queries and `JdbcTemplate` for inserts/updates requiring `GeneratedKeyHolder`.
- `OwnerRepository` is the most complex — it manages pets and visits as aggregated children within the owner aggregate.
- The `VetRepository` loads specialties for vets; `SpecialtyRepository` and `PetTypeRepository` are simpler CRUD repositories.
- All packages follow an open-in-package convention (e.g., `org.springframework.samples.petclinic.owner`) where domain classes, interfaces, and implementations live in the same package.