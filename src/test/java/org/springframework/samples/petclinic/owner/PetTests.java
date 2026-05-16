package org.springframework.samples.petclinic.owner;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PetTests {

	private Pet pet;

	private PetType dog;

	@BeforeEach
	void setUp() {
		pet = new Pet();
		pet.setName("Fido");
		pet.setBirthDate(LocalDate.of(2023, 1, 1));
		dog = new PetType();
		dog.setId(1);
		dog.setName("dog");
		pet.setType(dog);
	}

	@Test
	void shouldSetAndGetName() {
		pet.setName("Buddy");
		assertThat(pet.getName()).isEqualTo("Buddy");
	}

	@Test
	void shouldSetAndGetBirthDate() {
		LocalDate date = LocalDate.of(2022, 6, 15);
		pet.setBirthDate(date);
		assertThat(pet.getBirthDate()).isEqualTo(date);
	}

	@Test
	void shouldSetAndGetType() {
		PetType cat = new PetType();
		cat.setName("cat");
		pet.setType(cat);
		assertThat(pet.getType().getName()).isEqualTo("cat");
	}

	@Test
	void shouldAddVisit() {
		Visit visit = new Visit();
		visit.setDescription("rabies shot");
		visit.setDate(LocalDate.of(2024, 1, 15));

		pet.addVisit(visit);

		assertThat(pet.getVisits()).contains(visit);
	}

	@Test
	void shouldAddMultipleVisits() {
		Visit visit1 = new Visit();
		visit1.setDescription("rabies shot");
		visit1.setDate(LocalDate.of(2024, 1, 15));

		Visit visit2 = new Visit();
		visit2.setDescription("checkup");
		visit2.setDate(LocalDate.of(2024, 6, 1));

		pet.addVisit(visit1);
		pet.addVisit(visit2);

		assertThat(pet.getVisits()).hasSize(2);
		assertThat(pet.getVisits()).containsExactly(visit1, visit2);
	}

	@Test
	void shouldBeNewWhenIdIsNull() {
		assertThat(pet.isNew()).isTrue();
	}

	@Test
	void shouldNotBeNewWhenIdIsSet() {
		pet.setId(1);
		assertThat(pet.isNew()).isFalse();
	}

}