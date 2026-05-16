package org.springframework.samples.petclinic.rest.contract;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.rest.dto.PetCreateDto;
import org.springframework.samples.petclinic.rest.dto.PetDto;
import org.springframework.samples.petclinic.rest.dto.VisitCreateDto;
import org.springframework.samples.petclinic.rest.dto.VisitDto;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PetAndVisitApiContractTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void listPetsForOwner() {
		ResponseEntity<PetDto[]> response = restTemplate.getForEntity("/api/v1/owners/1/pets", PetDto[].class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
	}

	@Test
	void getPetDetail() {
		ResponseEntity<PetDto> response = restTemplate.getForEntity("/api/v1/owners/1/pets/1", PetDto.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		PetDto pet = response.getBody();
		assertThat(pet).isNotNull();
		assertThat(pet.id()).isNotNull();
		assertThat(pet.name()).isNotNull();
		assertThat(pet.birthDate()).isNotNull();
		assertThat(pet.type()).isNotNull();
	}

	@Test
	void createPetForOwner() {
		PetCreateDto dto = new PetCreateDto("NewDog", LocalDate.of(2023, 6, 15), 1);
		ResponseEntity<PetDto> response = restTemplate.postForEntity("/api/v1/owners/6/pets", dto, PetDto.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().name()).isEqualTo("NewDog");
	}

	@Test
	void createPetValidatesRequiredFields() {
		PetCreateDto blankDto = new PetCreateDto("", null, null);
		ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/owners/6/pets", blankDto, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void updatePetForOwner() {
		PetCreateDto dto = new PetCreateDto("UpdatedName", LocalDate.of(2020, 1, 1), 1);
		ResponseEntity<PetDto> response = restTemplate.exchange("/api/v1/owners/6/pets/7", HttpMethod.PUT,
				new HttpEntity<>(dto), PetDto.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().name()).isEqualTo("UpdatedName");
	}

	@Test
	void listVisitsForPet() {
		ResponseEntity<VisitDto[]> response = restTemplate.getForEntity("/api/v1/owners/6/pets/7/visits",
				VisitDto[].class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
	}

	@Test
	void createVisitForPet() {
		VisitCreateDto dto = new VisitCreateDto(LocalDate.of(2024, 12, 1), "Annual checkup");
		ResponseEntity<VisitDto> response = restTemplate.postForEntity("/api/v1/owners/6/pets/7/visits", dto,
				VisitDto.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().description()).isEqualTo("Annual checkup");
	}

	@Test
	void createVisitValidatesDescriptionNotBlank() {
		VisitCreateDto blankDto = new VisitCreateDto(LocalDate.of(2024, 1, 1), "");
		ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/owners/6/pets/7/visits", blankDto,
				String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void getPetForNonExistentOwnerReturnsNotFound() {
		ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/owners/999/pets", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void getVisitForNonExistentPetReturnsNotFound() {
		ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/owners/6/pets/999/visits", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

}