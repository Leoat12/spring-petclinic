/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.owner.PetTypeRepository;
import org.springframework.samples.petclinic.testcontainers.BasePostgresIntegrationTest;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.testcontainers.DockerClientFactory;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.Optional;

@DisabledInNativeImage
class PostgresIntegrationTests extends BasePostgresIntegrationTest {

	@Autowired
	private VetRepository vets;

	@Autowired
	private OwnerRepository ownerRepository;

	@Autowired
	private PetTypeRepository petTypeRepository;

	@Autowired
	private TestRestTemplate rest;

	@BeforeAll
	static void dockerAvailable() {
		assumeTrue(DockerClientFactory.instance().isDockerAvailable(), "Docker not available");
	}

	@Test
	void shouldFindAllVets() {
		assertThat(vets.findAll()).isNotEmpty();
	}

	@Test
	void shouldFindVetById() {
		Optional<Vet> vet = vets.findById(1);
		assertThat(vet).isPresent();
		assertThat(vet.get().getFirstName()).isEqualTo("James");
	}

	@Test
	void shouldFindOwnersByLastName() {
		Page<Owner> results = ownerRepository.findByLastNameStartingWith("Davis", Pageable.unpaged());
		assertThat(results).hasSize(2);
	}

	@Test
	void shouldFindOwnerById() {
		Optional<Owner> owner = ownerRepository.findById(1);
		assertThat(owner).isPresent();
		assertThat(owner.get().getFirstName()).isEqualTo("George");
	}

	@Test
	void shouldFindAllPetTypes() {
		assertThat(petTypeRepository.findPetTypes()).isNotEmpty();
	}

	@Test
	void ownerDetails() {
		ResponseEntity<String> result = rest.exchange(RequestEntity.get("/owners/1").build(), String.class);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void ownerList() {
		ResponseEntity<String> result = rest.exchange(RequestEntity.get("/owners?lastName=").build(), String.class);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

}