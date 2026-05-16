package org.springframework.samples.petclinic.owner;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.service.EntityUtils;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
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
		owners.save(owner);
		assertThat(owner.getId()).isNotZero();

		ownersPage = owners.findByLastNameStartingWith("Schultz", pageable);
		assertThat(ownersPage.getTotalElements()).isEqualTo(found + 1);
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

}