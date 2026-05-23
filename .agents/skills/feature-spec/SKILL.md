---
name: feature-spec
description: |
  Scaffold a new feature specification directory from the project roadmap — creating a git branch, asking the user about requirements, and writing requirements.md, plan.md, and validation.md. Use this skill when the user asks you to START or CREATE a feature spec, BEGIN a new phase, or SCAFFOLD feature work (e.g., "start the next feature", "create a feature spec", "set up a new phase"). Do NOT use this skill when the user is simply ASKING about the roadmap (e.g., "what's next?", "show me the roadmap", "what phase are we on?") — just answer those questions directly by reading specs/roadmap.md.
---
 
# Feature Spec Skill
 
This skill scaffolds a new feature from the project roadmap. It reads the roadmap to find the next phase, creates a branch, gathers requirements from the user, and writes the spec files.
 
## Important: When to use this skill vs. just answering
 
This skill is for when the user wants to **create** spec files and start feature work. If the user is simply asking what's next on the roadmap (e.g., "what's next?", "what phase are we on?"), just read `specs/roadmap.md` and answer their question — do not run this full workflow.
 
## Workflow
 
### Step 1 — Find the next phase
 
1. Read `specs/roadmap.md`. Identify the **first phase where not all items are marked `[x]`** (i.e., the first incomplete phase). That is the next phase.
2. Note the phase number and title (e.g., "Phase 3 — Owner Email Feature").
 
If *all* phases are complete, tell the user the roadmap is fully done and ask what they'd like to do.
 
### Step 2 — Create a branch
 
Create a git branch named for the phase:
 
```
phase-<number>-<slug>
```
 
where `<number>` is the phase number and `<slug>` is a short kebab-case slug derived from the phase title (e.g., `phase-3-owner-email`).
 
### Step 3 — Gather requirements from the user
 
You **must** use the `question` tool (`AskUserQuestion`) to ask the user about the feature before writing any files. Group your questions into three sets, one for each output file. Present all three question groups in a single call so the user can answer everything at once.
 
**Requirements questions** (for `requirements.md`):
- What is the scope of this feature? What is explicitly in scope and out of scope?
- Are there any design decisions or trade-offs you've already made?
- What context should someone implementing this feature know? (e.g., constraints, conventions, related features)
 
**Plan questions** (for `plan.md`):
- How should the work be grouped into task groups? Are there logical milestones or dependencies?
- Any preferences on ordering — e.g., infrastructure first, then backend, then frontend?
 
**Validation questions** (for `validation.md`):
- How will we know this feature is complete and correct?
- Are there specific manual checks, commands, or scenarios that should pass?
- Any regression concerns — things that must keep working?
 
### Step 4 — Read context files
 
Read `specs/mission.md` and `specs/tech-stack.md` for guidance on project conventions, tech decisions, and goals. Use these to inform and validate the spec content.
 
### Step 5 — Create the spec directory and files
 
Create a directory under `specs/` named:
 
```
YYYY-MM-DD-<slug>
```
 
where `YYYY-MM-DD` is today's date and `<slug>` matches the branch slug.
 
Inside that directory, create three files:
 
#### `requirements.md`
 
```markdown
# Requirements — <Feature Title>
 
## Scope
 
<What this feature covers, in scope and out of scope>
 
## Decisions
 
<Design decisions and trade-offs>
 
## Context
 
<Background, constraints, conventions, related features>
```
 
Populate this from the user's answers to the requirements questions, enriched with relevant details from `specs/mission.md` and `specs/tech-stack.md`.
 
#### `plan.md`
 
```markdown
# Plan — <Feature Title>
 
## 1. <Task Group Name>
 
- [ ] <task>
- [ ] <task>
 
## 2. <Task Group Name>
 
- [ ] <task>
- [ ] <task>
 
...
```
 
Number each task group. Each task should be a concrete, actionable checkbox item. Structure the groups so earlier groups are prerequisites for later ones. Populate from the user's plan answers, using the roadmap checklist items as a starting point.
 
#### `validation.md`
 
```markdown
# Validation — <Feature Title>
 
## Acceptance Criteria
 
- [ ] <criterion>
- [ ] <criterion>
 
## Manual Checks
 
<Commands to run, URLs to visit, scenarios to verify>
 
## Regression Concerns
 
<Things that must keep working after this feature is merged>
```
 
Populate from the user's validation answers. Each acceptance criterion should be objectively verifiable.
 
### Important rules
 
- **Always ask before writing.** Use the `question` tool with all three question groups before creating any files. The user's answers shape the content.
- **Ground the specs in the roadmap.** The roadmap checklist items are the starting point — the spec expands and contextualizes them.
- **Reference mission and tech-stack.** Use `specs/mission.md` and `specs/tech-stack.md` to make sure the spec aligns with project goals and tech decisions.
- **Keep specs actionable.** Every checkbox item in `plan.md` and `validation.md` should be something someone can directly do or verify.