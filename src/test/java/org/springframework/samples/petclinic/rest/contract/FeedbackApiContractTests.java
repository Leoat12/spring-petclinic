package org.springframework.samples.petclinic.rest.contract;

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

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class FeedbackApiContractTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void createFeedbackReturnsCreatedWithLocation() {
		FeedbackCreateDto dto = new FeedbackCreateDto("Contract Test", "contract@test.com", "Contract test message");
		ResponseEntity<FeedbackDto> response = restTemplate.postForEntity("/api/v1/feedback", dto, FeedbackDto.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().id()).isNotNull();
		assertThat(response.getBody().name()).isEqualTo("Contract Test");
		assertThat(response.getBody().email()).isEqualTo("contract@test.com");
		assertThat(response.getBody().message()).isEqualTo("Contract test message");
		assertThat(response.getBody().createdAt()).isNotNull();
	}

	@Test
	void listFeedbackReturnsList() {
		FeedbackCreateDto dto = new FeedbackCreateDto("List Contract", "list@test.com", "List contract test");
		restTemplate.postForEntity("/api/v1/feedback", dto, FeedbackDto.class);

		ResponseEntity<List> response = restTemplate.getForEntity("/api/v1/feedback", List.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
	}

	@Test
	void createFeedbackValidatesRequiredFields() {
		FeedbackCreateDto blankDto = new FeedbackCreateDto("", null, "");
		ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/feedback", blankDto, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void createFeedbackValidatesEmailFormat() {
		FeedbackCreateDto invalidEmailDto = new FeedbackCreateDto("Test", "not-an-email", "Hello");
		ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/feedback", invalidEmailDto, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void createFeedbackValidatesMessageLength() {
		String longMessage = "a".repeat(2001);
		FeedbackCreateDto dto = new FeedbackCreateDto("Test", null, longMessage);
		ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/feedback", dto, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void createFeedbackAllowsNullEmail() {
		FeedbackCreateDto dto = new FeedbackCreateDto("No Email", null, "Hello world");
		ResponseEntity<FeedbackDto> response = restTemplate.postForEntity("/api/v1/feedback", dto, FeedbackDto.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().email()).isNull();
	}

}