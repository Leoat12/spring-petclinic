# Plan — Vue.js Frontend (Initial Scaffold)

## 1. Scaffold Vue.js App

- [ ] Initialize a Vue.js 3 project in `frontend/` using Vite (`npm create vue@latest`)
- [ ] Configure `tsconfig` and `vite.config.ts` for the project
- [ ] Add a minimal set of dependencies (Vue Router only; no state management or UI framework yet)
- [ ] Add `.gitignore` entries for `frontend/node_modules` and `frontend/dist`

## 2. Configure Proxy to Spring Boot Backend

- [ ] Configure Vite dev-server proxy to forward `/api/**` and `/vets.html` (etc.) requests to `http://localhost:8080`
- [ ] Verify that API calls from the Vue.js dev server reach the Spring Boot backend
- [ ] Document how to run both the Spring Boot app and the Vue.js dev server together for local development

## 3. Replace Welcome / Home Page

- [ ] Create a `HomeView.vue` component that replicates the content and styling of the existing Thymeleaf welcome page
- [ ] Use Bootstrap 5.3 classes to match the existing layout and visual style (include Bootstrap via CDN or npm)
- [ ] Configure Spring Boot to serve `frontend/dist/index.html` for the root route (`/`) when running in production (or redirect `/` to the Vue app)
- [ ] Deactivate or redirect the existing Thymeleaf welcome-page controller

## 4. Wire Up Vue Router

- [ ] Configure Vue Router with a `/` route pointing to `HomeView.vue`
- [ ] Add a catch-all or 404 fallback route
- [ ] Set up `<RouterView>` in `App.vue`
- [ ] Verify SPA navigation works: visiting `/` renders the Vue home page; visiting other routes (e.g., `/owners`) falls through to Spring Boot / Thymeleaf

## 5. Integrate Vue.js Build into Gradle

- [ ] Add a Gradle task (or `buildSrc` script) that runs `npm install` and `npm run build` in `frontend/`
- [ ] Configure the Spring Boot JAR to include `frontend/dist/` as static assets
- [ ] Verify: `./gradlew build` produces a working JAR that serves the Vue.js home page