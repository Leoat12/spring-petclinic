# Requirements — Gradle-Only Build

## Scope

**In scope:**
- Remove `pom.xml` and all Maven-specific files (Maven wrapper, `.mvn/` directory, `mvnw`, `mvnw.cmd`)
- Ensure the existing Gradle build covers all functionality previously handled by Maven: compilation, testing, code formatting (Spring Java Format), checkstyle, JaCoCo coverage, Spring Boot packaging, and any other Maven plugins currently in use
- Update the GitHub Actions CI workflow to use Gradle exclusively (no Maven steps or references)
- Update all documentation and scripts to reference Gradle commands only (README, contributing guides, Makefiles, shell scripts, etc.)

**Out of scope:**
- Adding new Gradle plugins or capabilities beyond what Maven currently provides
- Changing application behavior, dependencies, or configuration beyond build tooling
- Modifying the Dockerfile or container build process (already Gradle-based from Phase 0)

## Decisions

- **Gradle parity first**: verify and complete Gradle coverage *before* removing Maven files. This avoids a window where the build is broken.
- **Full removal**: all Maven artifacts are deleted — no keeping `pom.xml` around for reference. Git history preserves it.
- **Existing Gradle setup is the baseline**: the current `build.gradle` and wrapper are the foundation. Gaps are filled; Maven is not rewritten from scratch in Gradle.

## Context

- The project currently has a **dual Maven/Gradle build**. Phase 0 (Docker/CI) already uses Gradle for container builds, so the Gradle foundation is in place.
- Maven plugins to ensure Gradle parity for: Spring Boot Maven Plugin, Spring Java Format, Checkstyle, JaCoCo, Surefire (test execution), and the CycloneDX SBOM plugin.
- The tech stack doc confirms "Gradle only" as a tech decision — this phase enforces it.
- This is a learning portfolio project; the build should remain simple and idiomatic, serving as a reference for how a well-structured Gradle build looks.