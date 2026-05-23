# Plan — Fix Mutation Testing Coverage

## 1. Analyze Pitest Report

- [ ] Run `./gradlew pitest` locally and collect the full mutation report
- [ ] Categorize surviving mutations by module/layer (service, repository, controller, utility/config)
- [ ] Prioritize mutations by count and impact — identify which layers have the most surviving mutations

## 2. Kill Surviving Mutations in Service and Repository Layers

- [ ] Add or improve unit tests for surviving mutations in service classes
- [ ] Add or improve unit tests for surviving mutations in repository implementations (JdbcClient/SQL-based)
- [ ] Re-run Pitest and confirm mutation score improvement for these layers

## 3. Kill Surviving Mutations in Controller Layers

- [ ] Add or improve unit tests for surviving mutations in REST controllers
- [ ] Add or improve unit tests for surviving mutations in WebMvc controllers
- [ ] Re-run Pitest and confirm mutation score improvement for controllers

## 4. Kill Surviving Mutations in Utility and Configuration Classes

- [ ] Add or improve unit tests for surviving mutations in utility classes
- [ ] Add or improve unit tests for surviving mutations in Spring configuration classes
- [ ] Re-run Pitest and confirm mutation score improvement for utility/config layers

## 5. Verify Threshold and CI

- [ ] Run `./gradlew pitest` and verify overall mutation score is at or above 80%
- [ ] Run full `./gradlew build` and confirm all tests pass
- [ ] Push to CI and confirm the pipeline is green
- [ ] Manually review new tests to ensure they test meaningful behavior (not trivial mutation-killing hacks)