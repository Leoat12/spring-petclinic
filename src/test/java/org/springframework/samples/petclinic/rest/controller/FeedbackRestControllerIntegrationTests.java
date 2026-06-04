package org.springframework.samples.petclinic.rest.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.rest.dto.FeedbackCreateDto;
import org.springframework.samples.petclinic.rest.dto.FeedbackDto;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FeedbackRestControllerIntegrationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void shouldCreateFeedback() {
		FeedbackCreateDto dto = new FeedbackCreateDto("Jane Doe", "jane@example.com", "Great service!");
		ResponseEntity<FeedbackDto> response = restTemplate.postForEntity("/api/v1/feedback", dto, FeedbackDto.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().id()).isNotNull();
		assertThat(response.getBody().name()).isEqualTo("Jane Doe");
		assertThat(response.getBody().email()).isEqualTo("jane@example.com");
		assertThat(response.getBody().message()).isEqualTo("Great service!");
	}

	@Test
	void shouldCreateFeedbackWithoutEmail() {
		FeedbackCreateDto dto = new FeedbackCreateDto("Bob Smith", null, "Nice experience");
		ResponseEntity<FeedbackDto> response = restTemplate.postForEntity("/api/v1/feedback", dto, FeedbackDto.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().email()).isNull();
	}

	@Test
	void shouldRejectCreateFeedbackWithBlankName() {
		FeedbackCreateDto dto = new FeedbackCreateDto("", "test@example.com", "Hello");
		ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/feedback", dto, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void shouldRejectCreateFeedbackWithBlankMessage() {
		FeedbackCreateDto dto = new FeedbackCreateDto("Test", "test@example.com", "");
		ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/feedback", dto, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void shouldRejectCreateFeedbackWithInvalidEmail() {
		FeedbackCreateDto dto = new FeedbackCreateDto("Test", "not-an-email", "Hello");
		ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/feedback", dto, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void shouldListFeedback() {
		FeedbackCreateDto dto = new FeedbackCreateDto("Alice", "alice@example.com", "First feedback");
		restTemplate.postForEntity("/api/v1/feedback", dto, FeedbackDto.class);

		ResponseEntity<List> response = restTemplate.getForEntity("/api/v1/feedback", List.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody()).hasSizeGreaterThanOrEqualTo(1);
	}

}