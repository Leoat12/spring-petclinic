# Validation — Project Infrastructure

## Acceptance Criteria

- [ ] `docker build .` completes successfully and produces a container image
- [ ] `docker run` starts the app and the Thymeleaf UI is accessible at `http://localhost:8080`
- [ ] `./mvnw verify` passes locally (existing tests still green)
- [ ] Pushing to `main` triggers a green CI run (build, test, Checkstyle, JaCoCo)
- [ ] The container image is published to ghcr.io on pushes to `main`
- [ ] Dependabot is configured and creates PRs for dependency updates

## Manual Checks

1. **Docker build**: Run `docker build -t petclinic .` — should succeed
2. **Docker run**: Run `docker run -p 8080:8080 petclinic` — visit `http://localhost:8080` and verify the home page loads
3. **CI green**: Push a commit to `main` and check the GitHub Actions tab — the workflow should be green
4. **Checkstyle gate**: Introduce a style violation, push to a branch, and confirm CI fails on the Checkstyle step
5. **Dependabot**: Check the Insights > Dependency graph tab on GitHub to confirm Dependabot is active

## Regression Concerns

- The existing Maven build (`./mvnw verify`) must continue to pass — no changes to application code
- The existing Thymeleaf UI must work unchanged when running locally via Maven or inside the Docker container
- The Spring Boot `build-image` goal should still function if invoked directly, though the Dockerfile is the primary build path going forward