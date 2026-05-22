# Plan — Migrate to Spring Data JDBC

## 1. Dependency & Configuration Changes

- [ ] Add `spring-boot-starter-data-jdbc` dependency to `build.gradle`
- [ ] Remove `spring-boot-starter-data-jpa` dependency from `build.gradle`
- [ ] Remove JPA-specific configuration properties (e.g., `spring.jpa.*`, Hibernate dialect) from `application.properties`/`application.yml`
- [ ] Add Spring Data JDBC configuration properties (e.g., `spring.jdbc.*`) if needed
- [ ] Remove Hibernate-specific test configurations or listeners
- [ ] Verify the project compiles (it will fail at runtime until entities are migrated)

## 2. Entity Migration

- [ ] Migrate `Owner` from JPA `@Entity` to Spring Data JDBC `@Table` with `@Id` and `@MappedCollection` for related entities
- [ ] Migrate `Pet` from JPA `@Entity` to Spring Data JDBC `@Table` with `@Id` and mapped relationships
- [ ] Migrate `Visit` from JPA `@Entity` to Spring Data JDBC `@Table` with `@Id`
- [ ] Migrate `Vet` from JPA `@Entity` to Spring Data JDBC `@Table` with `@Id` and `@MappedCollection` for specialties
- [ ] Migrate `Specialty` from JPA `@Entity` to Spring Data JDBC `@Table` with `@Id`
- [ ] Remove all JPA-specific annotations (`@ManyToOne`, `@OneToMany`, `@JoinColumn`, `@JoinTable`, `@Column`, etc.)
- [ ] Ensure all entity field names match column names or use `@Column` (Spring Data JDBC) where they differ

## 3. Repository Migration

- [ ] Replace `OwnerRepository` (JPA `JpaRepository`) with Spring Data JDBC `CrudRepository<Owner, Integer>` 
- [ ] Add `JdbcClient`-based custom query methods for Owner (e.g., findByLastName)
- [ ] Replace `PetRepository` (JPA) with Spring Data JDBC `CrudRepository<Pet, Integer>`
- [ ] Add `JdbcClient`-based custom query methods for Pet (e.g., findByOwnerId)
- [ ] Replace `VisitRepository` (JPA) with Spring Data JDBC `CrudRepository<Visit, Integer>`
- [ ] Add `JdbcClient`-based custom query methods for Visit (e.g., findByPetId)
- [ ] Replace `VetRepository` (JPA) with Spring Data JDBC `CrudRepository<Vet, Integer>`
- [ ] Replace `SpecialtyRepository` (JPA) with Spring Data JDBC `CrudRepository<Specialty, Integer>`
- [ ] Remove all JPA-specific query methods (`@Query` with JPQL, derived query methods that use JPA features)
- [ ] Add `@MappedCollection` fields on aggregate roots where related entities are loaded together

## 4. Service Layer Updates

- [ ] Update `OwnerService` to work with new repository interfaces and `JdbcClient` queries
- [ ] Update `PetService` (or `ClinicService`) to work with new repository interfaces
- [ ] Update `VisitService` (or `ClinicService`) to work with new repository interfaces
- [ ] Update `VetService` (or `ClinicService`) to work with new repository interfaces
- [ ] Handle relationship loading explicitly (no lazy-loading — use eager fetch via joins or separate queries)

## 5. Controller Layer Updates

- [ ] Update REST controllers if any method signatures changed due to repository changes
- [ ] Update WebMvc controllers if any method signatures changed
- [ ] Verify DTO/projection classes still map correctly from the new entity structure

## 6. Testing & Verification

- [ ] Update all integration tests to work with Spring Data JDBC repositories
- [ ] Update Testcontainers tests (PostgreSQL) to use JDBC-based repos
- [ ] Run contract tests and verify REST API responses are unchanged
- [ ] Run mutation tests (Pitest) and verify coverage thresholds pass
- [ ] Remove any remaining JPA-specific test configurations or mocks
- [ ] Run `./gradlew build` and verify all tests pass on H2 (dev profile)
- [ ] Run CI and verify all tests pass on PostgreSQL (Testcontainers)