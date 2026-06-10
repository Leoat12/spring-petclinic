# Validation — Vue.js Frontend (Initial Scaffold)

## Acceptance Criteria

- [ ] Vue.js app scaffolded in `frontend/` with Vite and Vue 3
- [ ] Vite dev-server proxy forwards API requests to Spring Boot backend during local development
- [ ] Home page renders from Vue.js (not Thymeleaf) when visiting `/`
- [ ] Vue Router is wired up with at least a `/` route and a 404 fallback
- [ ] `./gradlew build` succeeds and produces a JAR that serves the Vue.js home page
- [ ] `./gradlew test` passes — all existing tests still pass

## Manual Checks

1. **Dev mode**: Start Spring Boot (`./gradlew bootRun`) and the Vue dev server (`cd frontend && npm run dev`), then visit `http://localhost:5173/` — the home page should render from Vue.js
2. **API proxy**: From the Vue dev server, navigate or make a request that hits `/api/v1/owners` — it should return JSON from the Spring Boot backend
3. **Thymeleaf coexistence**: Visit `/owners`, `/vets`, `/pets`, `/feedback` — each should still render from Thymeleaf (server-rendered HTML, not Vue)
4. **Production build**: Run `./gradlew build`, then `java -jar build/libs/*.jar` and visit `http://localhost:8080/` — the home page should render from the bundled Vue.js assets
5. **Vue Router**: Click the home link in the nav — it should navigate via Vue Router without a full page reload

## Regression Concerns

- All existing Thymeleaf pages (Owners list/detail, Vets, Pet/Visit forms, Feedback) must continue to work unchanged
- All existing REST API endpoints (`/api/v1/…`) must continue to return the same responses
- The Gradle build (`./gradlew build`) must still compile, test, and package the application correctly
- CI pipeline must continue to pass — no test failures or build breakage