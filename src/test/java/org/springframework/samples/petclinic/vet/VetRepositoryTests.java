package org.springframework.samples.petclinic.vet;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
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
@Import(JdbcClientVetRepository.class)
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

	@Test
	void shouldFindVetByIdWithSpecialties() {
		Optional<Vet> optionalVet = vets.findById(3);
		assertThat(optionalVet).isPresent();
		Vet vet = optionalVet.get();
		assertThat(vet.getNrOfSpecialties()).isGreaterThan(0);
		assertThat(vet.getSpecialties()).isNotEmpty();
		assertThat(vet.getSpecialties().get(0).getName()).isNotNull();
	}

	@Test
	void shouldLoadSpecialtiesForAllVets() {
		Collection<Vet> allVets = vets.findAll();
		assertThat(allVets).isNotEmpty();
		for (Vet vet : allVets) {
			if (vet.getId() == 3) {
				assertThat(vet.getNrOfSpecialties()).isEqualTo(2);
				assertThat(vet.getSpecialties().get(0).getId()).isNotNull();
				assertThat(vet.getSpecialties().get(1).getId()).isNotNull();
			}
		}
	}

	@Test
	void shouldFindVetByIdWithCorrectSpecialties() {
		Optional<Vet> vet1 = vets.findById(1);
		assertThat(vet1).isPresent();
		assertThat(vet1.get().getSpecialties()).isEmpty();

		Optional<Vet> vet3 = vets.findById(3);
		assertThat(vet3).isPresent();
		assertThat(vet3.get().getSpecialties()).isNotEmpty();
		assertThat(vet3.get().getSpecialties().get(0).getName()).isEqualTo("dentistry");
	}

}