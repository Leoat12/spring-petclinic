package org.springframework.samples.petclinic.owner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OwnerTests {

	private Owner owner;

	private PetType dog;

	@BeforeEach
	void setUp() {
		owner = new Owner();
		owner.setFirstName("George");
		owner.setLastName("Franklin");
		owner.setAddress("110 W. Liberty St.");
		owner.setCity("Madison");
		owner.setTelephone("6085551023");

		dog = new PetType();
		dog.setId(1);
		dog.setName("dog");
	}

	@Test
	void shouldAddNewPet() {
		Pet pet = new Pet();
		pet.setName("Fido");
		pet.setBirthDate(LocalDate.of(2023, 1, 1));
		pet.setType(dog);

		owner.addPet(pet);

		assertThat(owner.getPets()).contains(pet);
		assertThat(owner.getPet("Fido")).isNotNull();
	}

	@Test
	void shouldNotAddPetWithExistingId() {
		Pet pet = new Pet();
		pet.setId(1);
		pet.setName("Fido");

		owner.addPet(pet);

		assertThat(owner.getPets()).isEmpty();
	}

	@Test
	void shouldGetPetByNameCaseInsensitive() {
		Pet pet = new Pet();
		pet.setName("Fido");
		pet.setBirthDate(LocalDate.of(2023, 1, 1));
		pet.setType(dog);
		owner.addPet(pet);

		assertThat(owner.getPet("Fido")).isNotNull();
		assertThat(owner.getPet("fido")).isNotNull();
	}

	@Test
	void shouldGetPetById() {
		Pet pet = new Pet();
		pet.setName("Fido");
		pet.setBirthDate(LocalDate.of(2023, 1, 1));
		pet.setType(dog);
		owner.addPet(pet);
		pet.setId(7);

		assertThat(owner.getPet(7)).isNotNull();
		assertThat(owner.getPet(999)).isNull();
	}

	@Test
	void shouldReturnNullForNonExistentPet() {
		assertThat(owner.getPet("NonExistent")).isNull();
		assertThat(owner.getPet(999)).isNull();
	}

	@Test
	void shouldAddVisitToPet() {
		Pet pet = new Pet();
		pet.setName("Fido");
		pet.setBirthDate(LocalDate.of(2023, 1, 1));
		pet.setType(dog);
		owner.addPet(pet);
		pet.setId(7);

		Visit visit = new Visit();
		visit.setDate(LocalDate.of(2024, 1, 15));
		visit.setDescription("rabies shot");

		owner.addVisit(7, visit);

		assertThat(pet.getVisits()).contains(visit);
	}

	@Test
	void shouldThrowWhenAddingVisitToNonExistentPet() {
		Visit visit = new Visit();
		visit.setDescription("checkup");

		assertThatIllegalArgumentException().isThrownBy(() -> owner.addVisit(999, visit));
	}

	@Test
	void shouldThrowWhenVisitIsNull() {
		assertThatIllegalArgumentException().isThrownBy(() -> owner.addVisit(1, null));
	}

	@Test
	void shouldThrowWhenPetIdIsNull() {
		assertThatIllegalArgumentException().isThrownBy(() -> owner.addVisit(null, new Visit()));
	}

	@Test
	void shouldSetProperties() {
		owner.setAddress("New Address");
		assertThat(owner.getAddress()).isEqualTo("New Address");

		owner.setCity("New City");
		assertThat(owner.getCity()).isEqualTo("New City");

		owner.setTelephone("9999999999");
		assertThat(owner.getTelephone()).isEqualTo("9999999999");
	}

}