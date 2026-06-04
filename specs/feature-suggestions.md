# Feature Suggestions

Remaining features not yet added to the roadmap. Features 1, 2, 3, 4, 5, 6, and 11 have been promoted to roadmap phases 19–25 and are removed from this list.

---

## 7. Email Notifications

**Source:** Phase 9 (Owner Email), Phase 11 (Feedback Form)

| Spec | What was said |
|------|---------------|
| Phase 9 requirements | _"Email sending or notification features"_ explicitly out of scope |
| Phase 11 requirements | _"Email notifications on submission"_ explicitly out of scope |

**Suggestion:** Add email notification capability using Spring Boot's `spring-boot-starter-mail`. Two concrete use cases have already been identified:
1. Feedback submission confirmation — send a confirmation email to the submitter
2. Visit reminders — notify owners of upcoming vet visits (demonstrates scheduled tasks)

**Proposed scope:**
- Add `spring-boot-starter-mail` dependency
- Configure SMTP settings (with a `mail` profile using Mailpit/MailHog for local dev)
- Create a `NotificationService` in Kotlin with `sendFeedbackConfirmation()` and `sendVisitReminder()`
- Add a Testcontainers-based integration test for email delivery
- Document local dev email setup

---

## 8. API Rate Limiting

**Source:** Phase 11 (Feedback Form)

| Spec | What was said |
|------|---------------|
| Phase 11 requirements | _"Rate limiting"_ explicitly out of scope |

**Suggestion:** Add rate limiting to public-facing endpoints, especially `POST /api/v1/feedback` (anonymous submissions are an abuse vector). Use Spring Boot's `Bucket4j` or a simple in-memory rate limiter.

**Proposed scope:**
- Add `Bucket4j` dependency
- Rate-limit `POST /api/v1/feedback` (e.g., 5 submissions per IP per hour)
- Optionally rate-limit other public `POST` endpoints
- Add integration tests verifying 429 responses
- Configure limits via application properties

---

## 9. Performance & Load Testing

**Source:** Phase 5 (Testing Improvements)

| Spec | What was said |
|------|---------------|
| Phase 5 requirements | _"Performance/load testing"_ explicitly out of scope |

**Suggestion:** Add a performance testing suite using JMeter or Gatling to establish baselines and catch regressions. This becomes increasingly important as the Vue.js frontend depends on API response times.

**Proposed scope:**
- Add Gatling (Scala-based, integrates with Gradle) or k6 (script-based, CI-friendly)
- Baseline tests for key endpoints: `GET /api/v1/owners`, `GET /api/v1/vets`, `POST /api/v1/feedback`
- Add to CI as a non-blocking report (warn, don't fail)
- Document performance baselines

---

## 10. Docker Multi-Architecture Builds

**Source:** Phase 0 (Project Infrastructure)

| Spec | What was said |
|------|---------------|
| Phase 0 requirements | _"Multi-architecture builds (e.g., ARM64) — can be added later"_ |

**Suggestion:** Extend the Dockerfile and CI workflow to produce `linux/amd64` and `linux/arm64` images using `docker buildx`. This is relevant for developers on Apple Silicon and for deployment on ARM-based cloud instances.

**Proposed scope:**
- Update `Dockerfile` for multi-platform compatibility
- Add `docker buildx` step to GitHub Actions workflow
- Push multi-arch image to GitHub Container Registry
- Verify image runs on both `amd64` and `arm64`

---

## 12. HATEOAS / Hypermedia for REST API

**Source:** Phase 1 (REST API)

| Spec | What was said |
|------|---------------|
| Phase 1 requirements | _"HATEOAS / hypermedia"_ explicitly out of scope |

**Suggestion:** Add Spring HATEOAS links to existing REST API responses to make the API self-documenting and navigable. This is a teaching opportunity for REST maturity models.

**Proposed scope:**
- Add `spring-boot-starter-hateoas` dependency
- Enhance DTOs with `_links` containing navigation links (e.g., `self`, `owner` from a pet, `pets` from an owner)
- Update contract tests to verify link structure
- This can be done incrementally, starting with the `Owner` resource

---

## Summary: Priority Matrix

| # | Feature | Impact | Effort | Recommended Priority |
|---|---------|--------|--------|---------------------|
| 7 | Email Notifications | Medium | Medium | **Medium** — demonstrates Spring Mail |
| 8 | API Rate Limiting | Low | Low | **Low** — nice-to-have for feedback |
| 9 | Performance Testing | Medium | Medium | **Low** — baseline, not blocking |
| 10 | Multi-Arch Docker Builds | Low | Low | **Low** — CI enhancement |
| 12 | HATEOAS for REST API | Low | Medium | **Low** — educational, not urgent |