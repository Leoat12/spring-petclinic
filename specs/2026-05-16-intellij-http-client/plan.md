# Plan — IntelliJ HTTP Client File

## 1. Infrastructure

- [ ] Create `petclinic.http` in the project root with environment variables (`baseUrl`, commonly used IDs)
- [ ] Add a file header comment explaining the file's purpose and how to run it in IntelliJ IDEA

## 2. Owners

- [ ] Add `GET /api/v1/owners` — list all owners
- [ ] Add `GET /api/v1/owners/{id}` — get owner by ID
- [ ] Add `POST /api/v1/owners` — create a new owner
- [ ] Add `PUT /api/v1/owners/{id}` — update an existing owner

## 3. Pets

- [ ] Add `GET /api/v1/owners/{ownerId}/pets` — list pets for an owner
- [ ] Add `GET /api/v1/owners/{ownerId}/pets/{petId}` — get a specific pet
- [ ] Add `POST /api/v1/owners/{ownerId}/pets` — add a pet to an owner
- [ ] Add `PUT /api/v1/owners/{ownerId}/pets/{petId}` — update a pet

## 4. Visits

- [ ] Add `GET /api/v1/owners/{ownerId}/pets/{petId}/visits` — list visits for a pet
- [ ] Add `POST /api/v1/owners/{ownerId}/pets/{petId}/visits` — add a visit to a pet

## 5. Vets

- [ ] Add `GET /api/v1/vets` — list all vets

## 6. Specialties

- [ ] Add `GET /api/v1/vets/specialties` — list all specialties (if applicable endpoint exists)

## 7. Verification

- [ ] Run every request in the file against a fresh local `dev` profile to confirm they all succeed