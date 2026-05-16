# Requirements — IntelliJ HTTP Client File

## Scope

**In scope:**
- Create a `petclinic.http` file in the project root covering all 14 REST endpoints from Phase 1
- Use IntelliJ HTTP Client variables for the base URL (and other reusable values)
- Organize requests by domain entity (Owners, Pets, Visits, Vets)
- Include sample data IDs and payloads matching the H2 seed data

**Out of scope:**
- Error/edge-case request scenarios (400, 404, etc.) — only happy paths
- Authentication or authorization headers (not applicable yet)
- Automated testing of the `.http` file — validation is manual

## Decisions

- **File location:** Project root as `petclinic.http`
- **Variables:** Use IntelliJ HTTP Client `{{variable}}` syntax for the base URL (`{{baseUrl}}`) and any commonly referenced IDs, so requests are easy to adapt across environments
- **Data:** IDs and payloads reference the H2 seed data loaded by the `dev` profile, ensuring reproducibility locally
- **Format:** Follow standard IntelliJ HTTP Client conventions (request blocks separated by `###`)

## Context

- The REST API layer was added in Phase 1; all endpoints live under `/api/v1/` and return JSON
- The H2 `dev` profile loads seed data on startup, providing predictable IDs for sample requests
- No authentication is required for the current API
- This is a developer-ergonomics feature: the `.http` file makes it easy to exercise the API from IntelliJ IDEA after each feature change
- No existing code is modified; this is a purely additive feature