package org.springframework.samples.petclinic.rest.controller;

import static org.assertj.core.api.Assertions.assertThat;

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
import org.springframework.samples.petclinic.rest.dto.OwnerCreateDto;
import org.springframework.samples.petclinic.rest.dto.OwnerDto;
import org.springframework.samples.petclinic.rest.dto.OwnerUpdateDto;
import org.springframework.samples.petclinic.rest.dto.PagedResultDto;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class OwnerRestControllerIntegrationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void shouldListOwners() {
		ResponseEntity<PagedResultDto> response = restTemplate.getForEntity("/api/v1/owners", PagedResultDto.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().totalElements()).isGreaterThan(0);
	}

	@Test
	void shouldGetOwnerById() {
		ResponseEntity<OwnerDto> response = restTemplate.getForEntity("/api/v1/owners/1", OwnerDto.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().firstName()).isNotNull();
	}

	@Test
	void shouldReturnNotFoundForNonExistentOwner() {
		ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/owners/999", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void shouldCreateOwner() {
		OwnerCreateDto dto = new OwnerCreateDto("John", "Doe", "123 Main St", "Springfield", "5551234567",
				"john@doe.com");

		ResponseEntity<OwnerDto> response = restTemplate.postForEntity("/api/v1/owners", dto, OwnerDto.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().firstName()).isEqualTo("John");
		assertThat(response.getBody().id()).isNotNull();
	}

	@Test
	void shouldRejectCreateOwnerWithInvalidData() {
		OwnerCreateDto dto = new OwnerCreateDto("", "", "", "", "abc", "invalid-email");

		ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/owners", dto, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void shouldUpdateOwner() {
		OwnerUpdateDto dto = new OwnerUpdateDto("UpdatedName", "Franklin", "110 W. Liberty St.", "Madison",
				"6085551023", "updated@email.com");

		ResponseEntity<OwnerDto> response = restTemplate.exchange("/api/v1/owners/1", HttpMethod.PUT,
				new HttpEntity<>(dto), OwnerDto.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().firstName()).isEqualTo("UpdatedName");
	}

	@Test
	void shouldReturnNotFoundWhenUpdatingNonExistentOwner() {
		OwnerUpdateDto dto = new OwnerUpdateDto("Updated", "Franklin", "110 W. Liberty St.", "Madison", "6085551023",
				null);

		ResponseEntity<String> response = restTemplate.exchange("/api/v1/owners/999", HttpMethod.PUT,
				new HttpEntity<>(dto), String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void shouldDeleteOwner() {
		OwnerCreateDto dto = new OwnerCreateDto("Delete", "Me", "123 St", "City", "1234567890", null);
		ResponseEntity<OwnerDto> createResponse = restTemplate.postForEntity("/api/v1/owners", dto, OwnerDto.class);
		Integer id = createResponse.getBody().id();

		ResponseEntity<Void> deleteResponse = restTemplate.exchange("/api/v1/owners/" + id, HttpMethod.DELETE, null,
				Void.class);
		assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	@Test
	void shouldReturnNotFoundWhenDeletingNonExistentOwner() {
		ResponseEntity<String> deleteResponse = restTemplate.exchange("/api/v1/owners/999", HttpMethod.DELETE, null,
				String.class);
		assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void shouldCreateOwnerWithEmail() {
		OwnerCreateDto dto = new OwnerCreateDto("Email", "Test", "789 Elm St", "Boston", "6175551234",
				"email@test.com");
		ResponseEntity<OwnerDto> response = restTemplate.postForEntity("/api/v1/owners", dto, OwnerDto.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().email()).isEqualTo("email@test.com");
	}

	@Test
	void shouldCreateOwnerWithoutEmail() {
		OwnerCreateDto dto = new OwnerCreateDto("No", "Email", "456 Pine St", "Chicago", "3125551234", null);
		ResponseEntity<OwnerDto> response = restTemplate.postForEntity("/api/v1/owners", dto, OwnerDto.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().email()).isNull();
	}

	@Test
	void shouldUpdateOwnerEmail() {
		OwnerUpdateDto dto = new OwnerUpdateDto("George", "Franklin", "110 W. Liberty St.", "Madison", "6085551023",
				"new@email.com");
		ResponseEntity<OwnerDto> response = restTemplate.exchange("/api/v1/owners/1", HttpMethod.PUT,
				new HttpEntity<>(dto), OwnerDto.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().email()).isEqualTo("new@email.com");
	}

	@Test
	void shouldRejectCreateOwnerWithInvalidEmail() {
		OwnerCreateDto dto = new OwnerCreateDto("Bad", "Email", "123 St", "City", "1234567890", "not-an-email");
		ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/owners", dto, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

}