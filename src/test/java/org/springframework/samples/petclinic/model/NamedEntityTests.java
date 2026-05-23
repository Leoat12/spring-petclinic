package org.springframework.samples.petclinic.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.owner.PetType;

class NamedEntityTests {

	@Test
	void shouldReturnNameInToString() {
		PetType petType = new PetType();
		petType.setName("cat");
		assertThat(petType.toString()).isEqualTo("cat");
	}

	@Test
	void shouldReturnNullPlaceholderInToStringWhenNameIsNull() {
		PetType petType = new PetType();
		assertThat(petType.toString()).isEqualTo("<null>");
	}

}