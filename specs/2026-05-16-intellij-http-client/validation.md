# Validation — IntelliJ HTTP Client File

## Acceptance Criteria

- [ ] `petclinic.http` exists in the project root
- [ ] The file uses `{{baseUrl}}` and other variables for portability
- [ ] All 14 REST API endpoints are represented with happy-path requests
- [ ] Requests are organized by domain (Owners, Pets, Visits, Vets) with clear `###` separators and comments
- [ ] Sample data IDs and payloads match the H2 seed data

## Manual Checks

1. Start the application with the `dev` profile: `./gradlew bootRun`
2. Open `petclinic.http` in IntelliJ IDEA
3. Run each request individually and verify a successful HTTP response
4. Verify all 14 endpoints are covered by reviewing the request list against the Phase 1 REST controllers

## Regression Concerns

This is a purely additive feature. No existing code is modified, so no regressions are expected.