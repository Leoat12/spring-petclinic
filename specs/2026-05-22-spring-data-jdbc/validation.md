# Validation — Migrate to Spring Data JDBC

## Acceptance Criteria

- [ ] All existing integration tests pass with Spring Data JDBC repositories
- [ ] All contract tests pass — REST API request/response shapes are unchanged
- [ ] Mutation testing (Pitest) coverage threshold is met
- [ ] `./gradlew build` succeeds with no JPA dependencies in the classpath
- [ ] No `@Entity`, `@ManyToOne`, `@OneToMany`, `@JoinColumn`, or other JPA annotations remain in the codebase
- [ ] No `spring-boot-starter-data-jpa` dependency in `build.gradle`
- [ ] Application starts successfully with H2 (dev profile) and PostgreSQL (Testcontainers)
- [ ] All five domain entities use `@Table` and `@Id` (Spring Data JDBC annotations)
- [ ] Repository interfaces extend `CrudRepository` (Spring Data JDBC) or use `JdbcClient`
- [ ] No JPA `JpaRepository` interfaces remain

## Manual Checks

- Run `./gradlew build` — should succeed with all tests green
- Run `./gradlew test` with PostgreSQL Testcontainers profile — should succeed
- Start the application with `dev` profile and verify:
  - `curl http://localhost:9976/api/v1/owners` returns the same JSON structure as before
  - `curl http://localhost:9976/api/v1/vets` returns the same JSON structure as before
  - `curl http://localhost:9976/api/v1/pets` returns the same JSON structure as before
  - `curl http://localhost:9976/api/v1/visits` returns the same JSON structure as before
- Verify Thymeleaf UI pages still render correctly:
  - Owners list and detail pages
  - Vets list page
  - Pet and Visit forms
- Run `grep -r "javax.persistence\|jakarta.persistence\|spring-boot-starter-data-jpa" src/ build.gradle` — should return no results

## Regression Concerns

- REST API response JSON shapes must not change (breaking the API contract)
- H2 dev profile must still work for rapid local development
- PostgreSQL Testcontainers tests must still work
- Existing Thymeleaf views must render correctly (they depend on the service layer, which is being updated)
- The `petclinic.http` file (Phase 4) must still work against the running application
- Checkstyle and JaCoCo configurations should not be affected but must still pass