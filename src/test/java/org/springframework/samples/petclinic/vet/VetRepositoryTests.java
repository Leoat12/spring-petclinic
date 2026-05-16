package org.springframework.samples.petclinic.vet;

import static org.assertj.core.api.Assertions.assertThat;

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
class VetRepositoryTests {

	@Autowired
	protected VetRepository vets;

	@Test
	void shouldFindAllVets() {
		Collection<Vet> allVets = vets.findAll();
		assertThat(allVets).isNotEmpty();
	}

	@Test
	void shouldFindVetsWithSpecialties() {
		Collection<Vet> allVets = vets.findAll();
		Vet vet = EntityUtils.getById(allVets, Vet.class, 3);
		assertThat(vet.getLastName()).isEqualTo("Douglas");
		assertThat(vet.getNrOfSpecialties()).isEqualTo(2);
		assertThat(vet.getSpecialties().get(0).getName()).isEqualTo("dentistry");
		assertThat(vet.getSpecialties().get(1).getName()).isEqualTo("surgery");
	}

	@Test
	void shouldFindAllVetsPaginated() {
		Page<Vet> page = vets.findAll(Pageable.ofSize(10));
		assertThat(page.getTotalElements()).isGreaterThan(0);
		assertThat(page.getContent()).isNotEmpty();
	}

	@Test
	void shouldFindVetById() {
		Optional<Vet> optionalVet = vets.findById(1);
		assertThat(optionalVet).isPresent();
		assertThat(optionalVet.get().getFirstName()).isEqualTo("James");
	}

	@Test
	void shouldReturnEmptyForNonExistentVet() {
		Optional<Vet> optionalVet = vets.findById(999);
		assertThat(optionalVet).isEmpty();
	}

	@Test
	void shouldFindVetCaching() {
		Collection<Vet> firstCall = vets.findAll();
		Collection<Vet> secondCall = vets.findAll();
		assertThat(secondCall).hasSameSizeAs(firstCall);
	}

}