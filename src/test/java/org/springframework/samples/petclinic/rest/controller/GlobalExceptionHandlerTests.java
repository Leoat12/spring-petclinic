package org.springframework.samples.petclinic.rest.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class GlobalExceptionHandlerTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void shouldReturnNotFoundResponseForResourceNotFoundException() {
		ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/owners/999", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getHeaders().getContentType()).isNotNull();
	}

	@Test
	void shouldReturnValidationErrorForInvalidInput() {
		String json = "{\"firstName\":\"\",\"lastName\":\"\",\"address\":\"\",\"city\":\"\",\"telephone\":\"abc\"}";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(json, headers);

		ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/owners", entity, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void shouldReturnNotFoundForInvalidUrl() {
		ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/nonexistent", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

}