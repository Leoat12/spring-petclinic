package org.springframework.samples.petclinic.owner;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.service.EntityUtils;
import org.springframework.transaction.annotation.Transactional;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(JdbcClientOwnerRepository.class)
class OwnerRepositoryTests {

	@Autowired
	protected OwnerRepository owners;

	private final Pageable pageable = Pageable.unpaged();

	@Test
	void shouldFindOwnersByLastName() {
		Page<Owner> results = owners.findByLastNameStartingWith("Davis", pageable);
		assertThat(results).hasSize(2);

		results = owners.findByLastNameStartingWith("Daviss", pageable);
		assertThat(results).isEmpty();
	}

	@Test
	void shouldFindOwnersByLastNamePartialMatch() {
		Page<Owner> results = owners.findByLastNameStartingWith("Da", pageable);
		assertThat(results.getTotalElements()).isGreaterThanOrEqualTo(2);
	}

	@Test
	void shouldFindAllOwners() {
		Page<Owner> results = owners.findAll(pageable);
		assertThat(results.getTotalElements()).isGreaterThan(0);
	}

	@Test
	void shouldFindSingleOwnerWithPet() {
		Optional<Owner> optionalOwner = owners.findById(1);
		assertThat(optionalOwner).isPresent();
		Owner owner = optionalOwner.get();
		assertThat(owner.getLastName()).startsWith("Franklin");
		assertThat(owner.getPets()).hasSize(1);
		assertThat(owner.getPets().get(0).getType()).isNotNull();
		assertThat(owner.getPets().get(0).getType().getName()).isEqualTo("cat");
	}

	@Test
	void shouldFindOwnerById() {
		Optional<Owner> optionalOwner = owners.findById(1);
		assertThat(optionalOwner).isPresent();
		assertThat(optionalOwner.get().getFirstName()).isEqualTo("George");
	}

	@Test
	void shouldReturnEmptyForNonExistentOwner() {
		Optional<Owner> optionalOwner = owners.findById(999);
		assertThat(optionalOwner).isEmpty();
	}

	@Test
	@Transactional
	void shouldInsertOwner() {
		Page<Owner> ownersPage = owners.findByLastNameStartingWith("Schultz", pageable);
		int found = (int) ownersPage.getTotalElements();

		Owner owner = new Owner();
		owner.setFirstName("Sam");
		owner.setLastName("Schultz");
		owner.setAddress("4, Evans Street");
		owner.setCity("Wollongong");
		owner.setTelephone("4444444444");
		owner.setEmail("sam@schultz.com");
		owners.save(owner);
		assertThat(owner.getId()).isNotZero();

		ownersPage = owners.findByLastNameStartingWith("Schultz", pageable);
		assertThat(ownersPage.getTotalElements()).isEqualTo(found + 1);
	}

	@Test
	@Transactional
	void shouldInsertOwnerWithoutEmail() {
		Owner owner = new Owner();
		owner.setFirstName("NoEmail");
		owner.setLastName("Owner");
		owner.setAddress("123 Street");
		owner.setCity("City");
		owner.setTelephone("1234567890");
		owners.save(owner);
		assertThat(owner.getId()).isNotZero();

		Optional<Owner> loaded = owners.findById(owner.getId());
		assertThat(loaded).isPresent();
		assertThat(loaded.get().getEmail()).isNull();
	}

	@Test
	@Transactional
	void shouldPersistEmail() {
		Owner owner = new Owner();
		owner.setFirstName("Email");
		owner.setLastName("Test");
		owner.setAddress("123 Street");
		owner.setCity("City");
		owner.setTelephone("1234567890");
		owner.setEmail("email@test.com");
		owners.save(owner);

		Optional<Owner> loaded = owners.findById(owner.getId());
		assertThat(loaded).isPresent();
		assertThat(loaded.get().getEmail()).isEqualTo("email@test.com");
	}

	@Test
	@Transactional
	void shouldUpdateEmail() {
		Optional<Owner> optionalOwner = owners.findById(1);
		assertThat(optionalOwner).isPresent();
		Owner owner = optionalOwner.get();
		owner.setEmail("updated@email.com");
		owners.save(owner);

		Optional<Owner> reloaded = owners.findById(1);
		assertThat(reloaded).isPresent();
		assertThat(reloaded.get().getEmail()).isEqualTo("updated@email.com");
	}

	@Test
	@Transactional
	void shouldClearEmail() {
		Optional<Owner> optionalOwner = owners.findById(1);
		assertThat(optionalOwner).isPresent();
		Owner owner = optionalOwner.get();
		owner.setEmail(null);
		owners.save(owner);

		Optional<Owner> reloaded = owners.findById(1);
		assertThat(reloaded).isPresent();
		assertThat(reloaded.get().getEmail()).isNull();
	}

	@Test
	@Transactional
	void shouldUpdateOwner() {
		Optional<Owner> optionalOwner = owners.findById(1);
		assertThat(optionalOwner).isPresent();
		Owner owner = optionalOwner.get();
		String oldLastName = owner.getLastName();
		String newLastName = oldLastName + "X";

		owner.setLastName(newLastName);
		owners.save(owner);

		optionalOwner = owners.findById(1);
		assertThat(optionalOwner).isPresent();
		assertThat(optionalOwner.get().getLastName()).isEqualTo(newLastName);
	}

	@Test
	@Transactional
	void shouldDeleteOwner() {
		Owner owner = new Owner();
		owner.setFirstName("Delete");
		owner.setLastName("Me");
		owner.setAddress("123 Street");
		owner.setCity("City");
		owner.setTelephone("1234567890");
		owners.save(owner);
		Integer id = owner.getId();
		assertThat(owners.existsById(id)).isTrue();

		owners.deleteById(id);
		assertThat(owners.existsById(id)).isFalse();
	}

	@Test
	void shouldCheckOwnerExists() {
		assertThat(owners.existsById(1)).isTrue();
		assertThat(owners.existsById(999)).isFalse();
	}

	@Test
	@Transactional
	void shouldInsertPetIntoOwner() {
		Optional<Owner> optionalOwner = owners.findById(6);
		assertThat(optionalOwner).isPresent();
		Owner owner6 = optionalOwner.get();

		int found = owner6.getPets().size();

		Pet pet = new Pet();
		pet.setName("bowser");
		Collection<PetType> types = owners.findAll(pageable)
			.getContent()
			.stream()
			.flatMap(o -> o.getPets().stream())
			.map(Pet::getType)
			.distinct()
			.toList();
		PetType petType = EntityUtils.getById(types.stream().toList(), PetType.class, 2);
		pet.setType(petType);
		pet.setBirthDate(LocalDate.now());
		owner6.addPet(pet);
		assertThat(owner6.getPets()).hasSize(found + 1);

		owners.save(owner6);

		optionalOwner = owners.findById(6);
		assertThat(optionalOwner).isPresent();
		owner6 = optionalOwner.get();
		assertThat(owner6.getPets()).hasSize(found + 1);
		Pet saved = owner6.getPet("bowser");
		assertThat(saved.getId()).isNotNull();
	}

	@Test
	@Transactional
	void shouldAddNewVisitForPet() {
		Optional<Owner> optionalOwner = owners.findById(6);
		assertThat(optionalOwner).isPresent();
		Owner owner6 = optionalOwner.get();

		Pet pet7 = owner6.getPet(7);
		int found = pet7.getVisits().size();
		Visit visit = new Visit();
		visit.setDescription("test");

		owner6.addVisit(pet7.getId(), visit);
		owners.save(owner6);

		assertThat(pet7.getVisits()).hasSize(found + 1).allMatch(v -> v.getId() != null);
	}

	@Test
	void shouldLoadOwnerWithPetAndVisitDetails() {
		Optional<Owner> optionalOwner = owners.findById(6);
		assertThat(optionalOwner).isPresent();
		Owner owner = optionalOwner.get();

		List<Pet> pets = owner.getPets();
		assertThat(pets).isNotEmpty();

		Pet pet = pets.get(0);
		assertThat(pet.getId()).isNotNull();
		assertThat(pet.getName()).isNotNull();
		assertThat(pet.getBirthDate()).isNotNull();
		assertThat(pet.getType()).isNotNull();
		assertThat(pet.getType().getId()).isNotNull();
		assertThat(pet.getType().getName()).isNotNull();
	}

	@Test
	void shouldLoadPetDetailsForOwnersViaFindByLastName() {
		Page<Owner> results = owners.findByLastNameStartingWith("Coleman", pageable);
		assertThat(results.getContent()).hasSize(1);
		Owner owner = results.getContent().get(0);
		assertThat(owner.getPets()).isNotEmpty();
		for (Pet pet : owner.getPets()) {
			assertThat(pet.getId()).isNotNull();
			assertThat(pet.getName()).isNotNull();
			assertThat(pet.getBirthDate()).isNotNull();
			assertThat(pet.getType()).isNotNull();
			assertThat(pet.getType().getId()).isNotNull();
		}
	}

	@Test
	void shouldLoadVisitDetailsForOwner() {
		Optional<Owner> optionalOwner = owners.findById(6);
		assertThat(optionalOwner).isPresent();
		Owner owner = optionalOwner.get();

		Pet pet7 = owner.getPet(7);
		assertThat(pet7).isNotNull();
		assertThat(pet7.getVisits()).isNotEmpty();
		Visit visit = pet7.getVisits().iterator().next();
		assertThat(visit.getId()).isNotNull();
		assertThat(visit.getDate()).isNotNull();
		assertThat(visit.getDescription()).isNotNull();
	}

	@Test
	void shouldLoadPetTypesForOwnersViaFindByLastName() {
		Page<Owner> results = owners.findByLastNameStartingWith("", pageable);
		assertThat(results.getContent()).isNotEmpty();
		for (Owner owner : results.getContent()) {
			for (Pet pet : owner.getPets()) {
				assertThat(pet.getType()).isNotNull();
				assertThat(pet.getType().getName()).isNotNull();
				assertThat(pet.getName()).isNotNull();
				assertThat(pet.getBirthDate()).isNotNull();
			}
		}
	}

	@Test
	void shouldLoadVisitDetailsForOwnersViaFindByLastName() {
		Page<Owner> results = owners.findByLastNameStartingWith("", pageable);
		assertThat(results.getContent()).isNotEmpty();
		boolean hasVisits = false;
		for (Owner owner : results.getContent()) {
			for (Pet pet : owner.getPets()) {
				assertThat(pet.getId()).isNotNull();
				for (Visit visit : pet.getVisits()) {
					assertThat(visit.getId()).isNotNull();
					assertThat(visit.getDate()).isNotNull();
					assertThat(visit.getDescription()).isNotNull();
					hasVisits = true;
				}
			}
		}
		assertThat(hasVisits).isTrue();
	}

	@Test
	void shouldLoadVisitDetailsForOwnerViaFindById() {
		Optional<Owner> optionalOwner = owners.findById(6);
		assertThat(optionalOwner).isPresent();
		Owner owner = optionalOwner.get();
		Pet pet7 = owner.getPet(7);
		assertThat(pet7).isNotNull();
		boolean foundVisitWithDetails = false;
		for (Visit visit : pet7.getVisits()) {
			assertThat(visit.getId()).isNotNull();
			assertThat(visit.getDate()).isNotNull();
			assertThat(visit.getDescription()).isNotNull();
			foundVisitWithDetails = true;
		}
		assertThat(foundVisitWithDetails).isTrue();
	}

	@Test
	@Transactional
	void shouldDeleteOwnerCascadesVisitsAndPets() {
		Owner owner = new Owner();
		owner.setFirstName("Cascade");
		owner.setLastName("Test");
		owner.setAddress("123 St");
		owner.setCity("City");
		owner.setTelephone("1234567890");
		owners.save(owner);
		Integer id = owner.getId();

		Collection<PetType> types = owners.findAll(pageable)
			.getContent()
			.stream()
			.flatMap(o -> o.getPets().stream())
			.map(Pet::getType)
			.distinct()
			.toList();
		PetType petType = EntityUtils.getById(types.stream().toList(), PetType.class, 1);

		Pet pet = new Pet();
		pet.setName("cascade_pet");
		pet.setBirthDate(LocalDate.now());
		pet.setType(petType);
		owner.addPet(pet);
		owners.save(owner);

		Visit visit = new Visit();
		visit.setDescription("cascade_visit");
		owner.addVisit(pet.getId(), visit);
		owners.save(owner);

		owners.deleteById(id);
		assertThat(owners.existsById(id)).isFalse();
	}

	@Test
	@Transactional
	void shouldSaveNewPetWithTypeAndIdSet() {
		Optional<Owner> optionalOwner = owners.findById(6);
		assertThat(optionalOwner).isPresent();
		Owner owner = optionalOwner.get();

		Collection<PetType> types = owners.findAll(pageable)
			.getContent()
			.stream()
			.flatMap(o -> o.getPets().stream())
			.map(Pet::getType)
			.distinct()
			.toList();
		PetType petType = EntityUtils.getById(types.stream().toList(), PetType.class, 2);

		Pet newPet = new Pet();
		newPet.setName("new_test_pet");
		newPet.setBirthDate(LocalDate.now());
		newPet.setType(petType);
		owner.addPet(newPet);
		owners.save(owner);

		Optional<Owner> reloaded = owners.findById(6);
		assertThat(reloaded).isPresent();
		Pet savedPet = reloaded.get().getPet("new_test_pet");
		assertThat(savedPet).isNotNull();
		assertThat(savedPet.getId()).isNotNull();
		assertThat(savedPet.getTypeId()).isNotNull();
	}

	@Test
	@Transactional
	void shouldUpdateExistingPetFields() {
		Optional<Owner> optionalOwner = owners.findById(6);
		assertThat(optionalOwner).isPresent();
		Owner owner = optionalOwner.get();

		Pet pet7 = owner.getPet(7);
		assertThat(pet7).isNotNull();
		String originalName = pet7.getName();

		Collection<PetType> types = owners.findAll(pageable)
			.getContent()
			.stream()
			.flatMap(o -> o.getPets().stream())
			.map(Pet::getType)
			.distinct()
			.toList();
		PetType newType = EntityUtils.getById(types.stream().toList(), PetType.class, 1);
		pet7.setName(originalName + "_updated");
		pet7.setType(newType);
		owners.save(owner);

		Optional<Owner> reloaded = owners.findById(6);
		assertThat(reloaded).isPresent();
		Pet updatedPet = reloaded.get().getPet(originalName + "_updated");
		assertThat(updatedPet).isNotNull();
		assertThat(updatedPet.getTypeId()).isEqualTo(newType.getId());
	}

	@Test
	@Transactional
	void shouldSaveVisitWithDateAndDescription() {
		Optional<Owner> optionalOwner = owners.findById(6);
		assertThat(optionalOwner).isPresent();
		Owner owner = optionalOwner.get();

		Pet pet7 = owner.getPet(7);
		LocalDate visitDate = LocalDate.of(2024, 6, 15);
		Visit visit = new Visit();
		visit.setDate(visitDate);
		visit.setDescription("annual checkup");
		owner.addVisit(pet7.getId(), visit);
		owners.save(owner);

		Optional<Owner> reloaded = owners.findById(6);
		assertThat(reloaded).isPresent();
		Pet reloadedPet = reloaded.get().getPet(7);
		boolean found = false;
		for (Visit v : reloadedPet.getVisits()) {
			if (v.getDescription().equals("annual checkup")) {
				assertThat(v.getDate()).isEqualTo(visitDate);
				assertThat(v.getId()).isNotNull();
				found = true;
			}
		}
		assertThat(found).isTrue();
	}

	@Test
	@Transactional
	void shouldRemovePetFromOwner() {
		Optional<Owner> optionalOwner = owners.findById(6);
		assertThat(optionalOwner).isPresent();
		Owner owner = optionalOwner.get();
		int initialPetCount = owner.getPets().size();
		assertThat(initialPetCount).isGreaterThan(0);

		Pet petToRemove = owner.getPets().get(0);
		owner.getPets().remove(petToRemove);
		owners.save(owner);

		Optional<Owner> reloaded = owners.findById(6);
		assertThat(reloaded).isPresent();
		assertThat(reloaded.get().getPets()).hasSize(initialPetCount - 1);
	}

	@Test
	void shouldLoadAllOwnersWithPetDetails() {
		Collection<Owner> allOwners = owners.findAll();
		assertThat(allOwners).isNotEmpty();
		for (Owner owner : allOwners) {
			for (Pet pet : owner.getPets()) {
				assertThat(pet.getId()).isNotNull();
				assertThat(pet.getName()).isNotNull();
				assertThat(pet.getBirthDate()).isNotNull();
				assertThat(pet.getType()).isNotNull();
				assertThat(pet.getType().getName()).isNotNull();
			}
		}
	}

	@Test
	void shouldLoadAllOwnersWithVisitDetails() {
		Collection<Owner> allOwners = owners.findAll();
		assertThat(allOwners).isNotEmpty();
		boolean hasVisits = false;
		for (Owner owner : allOwners) {
			for (Pet pet : owner.getPets()) {
				for (Visit visit : pet.getVisits()) {
					assertThat(visit.getId()).isNotNull();
					assertThat(visit.getDate()).isNotNull();
					assertThat(visit.getDescription()).isNotNull();
					hasVisits = true;
				}
			}
		}
		assertThat(hasVisits).isTrue();
	}

}