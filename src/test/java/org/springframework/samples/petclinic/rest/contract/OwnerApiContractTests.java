package org.springframework.samples.petclinic.rest.contract;

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
class OwnerApiContractTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void listOwnersReturnsPagedResult() {
		ResponseEntity<PagedResultDto> response = restTemplate.getForEntity("/api/v1/owners", PagedResultDto.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().content()).isNotNull();
		assertThat(response.getBody().pageNumber()).isGreaterThan(0);
		assertThat(response.getBody().pageSize()).isGreaterThan(0);
		assertThat(response.getBody().totalElements()).isGreaterThan(0);
		assertThat(response.getBody().totalPages()).isGreaterThan(0);
	}

	@Test
	void listOwnersSupportsPagination() {
		ResponseEntity<PagedResultDto> page1 = restTemplate.getForEntity("/api/v1/owners?page=1&size=2",
				PagedResultDto.class);
		assertThat(page1.getBody()).isNotNull();
		assertThat(page1.getBody().content().size()).isLessThanOrEqualTo(2);

		ResponseEntity<PagedResultDto> page2 = restTemplate.getForEntity("/api/v1/owners?page=2&size=2",
				PagedResultDto.class);
		assertThat(page2.getBody()).isNotNull();
	}

	@Test
	void getOwnerReturnsCompleteDto() {
		ResponseEntity<OwnerDto> response = restTemplate.getForEntity("/api/v1/owners/1", OwnerDto.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		OwnerDto owner = response.getBody();
		assertThat(owner).isNotNull();
		assertThat(owner.id()).isNotNull();
		assertThat(owner.firstName()).isNotNull();
		assertThat(owner.lastName()).isNotNull();
		assertThat(owner.address()).isNotNull();
		assertThat(owner.city()).isNotNull();
		assertThat(owner.telephone()).isNotNull();
	}

	@Test
	void createOwnerReturnsCreatedWithLocation() {
		OwnerCreateDto dto = new OwnerCreateDto("Jane", "Doe", "456 Oak Ave", "Portland", "5035551234");
		ResponseEntity<OwnerDto> response = restTemplate.postForEntity("/api/v1/owners", dto, OwnerDto.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().id()).isNotNull();
		assertThat(response.getBody().firstName()).isEqualTo("Jane");
		assertThat(response.getBody().lastName()).isEqualTo("Doe");
	}

	@Test
	void createOwnerValidatesAllFields() {
		OwnerCreateDto blankDto = new OwnerCreateDto("", "", "", "", "short");
		ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/owners", blankDto, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void updateOwnerModifiesExistingOwner() {
		OwnerUpdateDto dto = new OwnerUpdateDto("UpdatedName", "Franklin", "110 W. Liberty St.", "Madison",
				"6085551023");
		ResponseEntity<OwnerDto> response = restTemplate.exchange("/api/v1/owners/1", HttpMethod.PUT,
				new HttpEntity<>(dto), OwnerDto.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().firstName()).isEqualTo("UpdatedName");
	}

	@Test
	void deleteOwnerRemovesOwner() {
		OwnerCreateDto dto = new OwnerCreateDto("Delete", "Owner", "789 Pine", "Town", "9998887776");
		ResponseEntity<OwnerDto> createResponse = restTemplate.postForEntity("/api/v1/owners", dto, OwnerDto.class);
		Integer id = createResponse.getBody().id();

		ResponseEntity<Void> deleteResponse = restTemplate.exchange("/api/v1/owners/" + id, HttpMethod.DELETE, null,
				Void.class);
		assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity<String> getResponse = restTemplate.getForEntity("/api/v1/owners/" + id, String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

}