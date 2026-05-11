# Plan — Project Infrastructure

## 1. Docker Build

- [ ] Add a multi-stage `Dockerfile` (Maven build stage + JRE runtime stage)
- [ ] Add `.dockerignore` to exclude `.git`, `target/`, IDE files, and other unnecessary paths from the Docker context
- [ ] Verify: `docker build .` succeeds and produces a working image

## 2. GitHub Actions CI Pipeline

- [ ] Add GitHub Actions workflow (`.github/workflows/ci.yml`) that triggers on push to `main` and on pull requests
- [ ] Add Maven build and test step (`./mvnw verify`)
- [ ] Add Checkstyle enforcement step (`./mvnw checkstyle:check`)
- [ ] Add JaCoCo coverage reporting step
- [ ] Add Dependabot configuration (`.github/dependabot.yml`) for Maven and GitHub Actions dependency scanning
- [ ] Verify: pushing to `main` triggers a green CI run

## 3. Container Image Publish

- [ ] Add a CI job step to build the Docker image
- [ ] Add a CI job step to publish the container image to GitHub Container Registry (ghcr.io) on pushes to `main`
- [ ] Verify: after a push to `main`, the container image is published and pullable

## 4. Local Docker Verification

- [ ] Verify: `docker run` starts the app and the Thymeleaf UI is accessible at `localhost:8080`
- [ ] Verify: existing Maven build (`./mvnw verify`) still passes locally and in CI