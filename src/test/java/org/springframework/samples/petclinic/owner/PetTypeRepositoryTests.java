package org.springframework.samples.petclinic.owner;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.samples.petclinic.service.EntityUtils;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class PetTypeRepositoryTests {

	@Autowired
	protected PetTypeRepository petTypes;

	@Test
	void shouldFindAllPetTypes() {
		Collection<PetType> types = petTypes.findPetTypes();

		PetType petType1 = EntityUtils.getById(types, PetType.class, 1);
		assertThat(petType1.getName()).isEqualTo("cat");
		PetType petType4 = EntityUtils.getById(types, PetType.class, 4);
		assertThat(petType4.getName()).isEqualTo("snake");
	}

	@Test
	void shouldFindPetTypesOrderedByName() {
		List<PetType> types = petTypes.findPetTypes();
		assertThat(types).isSortedAccordingTo((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
	}

	@Test
	void shouldFindPetTypeById() {
		Optional<PetType> petType = petTypes.findById(1);
		assertThat(petType).isPresent();
		assertThat(petType.get().getName()).isEqualTo("cat");
	}

	@Test
	void shouldReturnEmptyForNonExistentPetType() {
		Optional<PetType> petType = petTypes.findById(999);
		assertThat(petType).isEmpty();
	}

	@Test
	@Transactional
	void shouldInsertPetType() {
		PetType petType = new PetType();
		petType.setName("hamster");
		petTypes.save(petType);
		assertThat(petType.getId()).isNotNull();

		Collection<PetType> types = petTypes.findPetTypes();
		assertThat(types.stream().anyMatch(t -> "hamster".equals(t.getName()))).isTrue();
	}

	@Test
	@Transactional
	void shouldUpdatePetType() {
		PetType petType = petTypes.findById(1).orElseThrow();
		petType.setName("feline");
		petTypes.save(petType);

		PetType updated = petTypes.findById(1).orElseThrow();
		assertThat(updated.getName()).isEqualTo("feline");
	}

	@Test
	@Transactional
	void shouldDeletePetType() {
		PetType petType = new PetType();
		petType.setName("ferret");
		petTypes.save(petType);
		Integer id = petType.getId();

		petTypes.deleteById(id);
		assertThat(petTypes.findById(id)).isEmpty();
	}

}