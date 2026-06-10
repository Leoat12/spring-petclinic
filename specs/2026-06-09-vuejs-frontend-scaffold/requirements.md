# Requirements — Vue.js Frontend (Initial Scaffold)

## Scope

This phase introduces Vue.js to the project as the foundation for incrementally replacing Thymeleaf views.

**In scope:**
- Scaffold a Vue.js 3 app in `frontend/` using Vite
- Configure a Vite dev-server proxy so API calls forward to the Spring Boot backend
- Replace the Welcome/home page Thymeleaf view with a Vue.js page
- Wire up Vue Router for SPA navigation
- Verify that the home page renders from Vue while all other pages remain served by Thymeleaf

**Out of scope:**
- Migrating any other pages (Owners, Vets, Pets, Visits, Feedback) — those are later phases
- Adding a CSS framework or component library beyond what's needed to match existing styling
- Adding state management (Pinia/Vuex)
- Adding authentication or authorization
- Changing any REST API endpoints

## Decisions

- **Vue 3 with Composition API** (`<script setup>` syntax) — the modern, idiomatic approach and the recommended default for new Vue.js projects.
- **Vite** as the build tool — fast dev server, first-class Vue 3 support, lightweight config.
- **Minimal dependencies** — no UI framework (e.g., Vuetify, PrimeVue) at this stage; use plain CSS / Bootstrap to match existing Thymeleaf styling.
- **Coexistence with Thymeleaf** — Vue.js and Thymeleaf must run side by side. Only the home page is replaced in this phase; all other routes remain server-rendered.

## Context

- The project is a **learning portfolio project** (see `specs/mission.md`); code should be idiomatic and well-structured so others can learn from it.
- The REST API (`/api/v1/…`) is already fully built and tested; the Vue.js app will consume it.
- Current styling uses **Bootstrap 5.3** and **Font Awesome 4.7** via WebJars. Vue.js pages should visually match the existing Thymeleaf pages to maintain consistency during the incremental migration.
- The Gradle build (`./gradlew build`) must continue to work. The Vue.js build should integrate into the Gradle lifecycle (e.g., via a `frontend` task or npm scripts invoked by Gradle).
- The application runs on **Spring Boot 4.0.3** with **Kotlin** backend code and **Spring Data JDBC** for persistence.
- See `specs/tech-stack.md` for the full technology matrix.