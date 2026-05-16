# Plan — PostgreSQL Only

## 1. Remove MySQL Dependencies & Configuration

- [ ] Remove MySQL driver dependency from `build.gradle`
- [ ] Remove `application-mysql.yml` (or equivalent MySQL profile config)
- [ ] Remove any MySQL-related properties from `application.yml` (datasource, dialect, etc.)
- [ ] Remove any MySQL-specific JPA dialect or Hibernate properties

## 2. Remove MySQL Schema & Data Scripts

- [ ] Delete MySQL-specific `schema.sql` and `data.sql` files (e.g., in `db/mysql/` or similar)
- [ ] Verify that H2 and PostgreSQL schema/data scripts are structurally aligned
- [ ] Ensure no references to MySQL scripts remain in configuration or code

## 3. Update Testcontainers to PostgreSQL Only

- [ ] Remove any MySQL Testcontainers dependencies from `build.gradle`
- [ ] Remove or update Testcontainers test classes that reference MySQL containers
- [ ] Ensure all Testcontainers-based integration tests use PostgreSQL exclusively
- [ ] Verify H2 in-memory tests continue to work unchanged

## 4. Update Documentation & CI

- [ ] Update `README.md` and any developer docs to reference PostgreSQL only (not MySQL)
- [ ] Update CI workflow if it references MySQL services or containers
- [ ] Update `specs/tech-stack.md` to reflect MySQL removal
- [ ] Update Docker Compose files (if any) to remove MySQL service

## 5. Verify & Clean Up

- [ ] Run `./gradlew build` and ensure all tests pass
- [ ] Verify application starts with H2 (default/dev profile)
- [ ] Verify Testcontainers PostgreSQL integration tests pass
- [ ] Search entire codebase for any remaining MySQL references (grep for `mysql`, `MySQL`, `com.mysql`)
- [ ] Remove any dead imports, comments, or TODOs referencing MySQL