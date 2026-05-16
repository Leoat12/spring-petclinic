package org.springframework.samples.petclinic.rest.contract;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.rest.dto.PagedResultDto;
import org.springframework.samples.petclinic.rest.dto.VetDto;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class VetApiContractTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void listVetsReturnsPagedResult() {
		ResponseEntity<PagedResultDto> response = restTemplate.getForEntity("/api/v1/vets", PagedResultDto.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().content()).isNotNull();
		assertThat(response.getBody().totalElements()).isGreaterThan(0);
	}

	@Test
	void listVetsSupportsPagination() {
		ResponseEntity<PagedResultDto> page1 = restTemplate.getForEntity("/api/v1/vets?page=1&size=1",
				PagedResultDto.class);
		assertThat(page1.getBody()).isNotNull();
		assertThat(page1.getBody().content().size()).isLessThanOrEqualTo(1);
	}

	@Test
	void getVetReturnsCompleteDto() {
		ResponseEntity<VetDto> response = restTemplate.getForEntity("/api/v1/vets/1", VetDto.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		VetDto vet = response.getBody();
		assertThat(vet).isNotNull();
		assertThat(vet.id()).isNotNull();
		assertThat(vet.firstName()).isNotNull();
		assertThat(vet.lastName()).isNotNull();
		assertThat(vet.specialties()).isNotNull();
	}

	@Test
	void getNonExistentVetReturnsNotFound() {
		ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/vets/999", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

}