package org.springframework.samples.petclinic.vet;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VetTests {

	private Vet vet;

	@BeforeEach
	void setUp() {
		vet = new Vet();
		vet.setFirstName("James");
		vet.setLastName("Carter");
	}

	@Test
	void shouldAddSpecialty() {
		Specialty specialty = new Specialty();
		specialty.setName("radiology");
		vet.addSpecialty(specialty);

		assertThat(vet.getNrOfSpecialties()).isEqualTo(1);
		assertThat(vet.getSpecialties()).contains(specialty);
	}

	@Test
	void shouldAddMultipleSpecialties() {
		Specialty radiology = new Specialty();
		radiology.setName("radiology");
		Specialty surgery = new Specialty();
		surgery.setName("surgery");
		vet.addSpecialty(radiology);
		vet.addSpecialty(surgery);

		assertThat(vet.getNrOfSpecialties()).isEqualTo(2);
		assertThat(vet.getSpecialties()).hasSize(2);
	}

	@Test
	void shouldReturnZeroSpecialtiesWhenNone() {
		assertThat(vet.getNrOfSpecialties()).isZero();
		assertThat(vet.getSpecialties()).isEmpty();
	}

	@Test
	void shouldReturnSpecialtiesSortedByName() {
		Specialty surgery = new Specialty();
		surgery.setName("surgery");
		Specialty dentistry = new Specialty();
		dentistry.setName("dentistry");

		vet.addSpecialty(surgery);
		vet.addSpecialty(dentistry);

		assertThat(vet.getSpecialties().get(0).getName()).isEqualTo("dentistry");
		assertThat(vet.getSpecialties().get(1).getName()).isEqualTo("surgery");
	}

	@Test
	void shouldSetAndGetFirstName() {
		vet.setFirstName("Helen");
		assertThat(vet.getFirstName()).isEqualTo("Helen");
	}

	@Test
	void shouldSetAndGetLastName() {
		vet.setLastName("Leary");
		assertThat(vet.getLastName()).isEqualTo("Leary");
	}

}