package org.springframework.samples.petclinic.rest.contract

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.TestRestTemplate
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.samples.petclinic.rest.dto.PagedResultDto
import org.springframework.samples.petclinic.rest.dto.VetDto

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class VetApiContractTests {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun listVetsReturnsPagedResult() {
        val response: ResponseEntity<PagedResultDto<Any>> = restTemplate.getForEntity("/api/v1/vets", PagedResultDto::class.java) as ResponseEntity<PagedResultDto<Any>>
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull()
        assertThat(response.body!!.content).isNotNull()
        assertThat(response.body!!.totalElements).isGreaterThan(0)
    }

    @Test
    fun listVetsSupportsPagination() {
        val page1: ResponseEntity<PagedResultDto<Any>> = restTemplate.getForEntity("/api/v1/vets?page=1&size=1", PagedResultDto::class.java) as ResponseEntity<PagedResultDto<Any>>
        assertThat(page1.body).isNotNull()
        assertThat(page1.body!!.content.size).isLessThanOrEqualTo(1)
    }

    @Test
    fun getVetReturnsCompleteDto() {
        val response: ResponseEntity<VetDto> = restTemplate.getForEntity("/api/v1/vets/1", VetDto::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val vet = response.body
        assertThat(vet).isNotNull()
        assertThat(vet!!.id).isNotNull()
        assertThat(vet.firstName).isNotNull()
        assertThat(vet.lastName).isNotNull()
        assertThat(vet.specialties).isNotNull()
    }

    @Test
    fun getNonExistentVetReturnsNotFound() {
        val response: ResponseEntity<String> = restTemplate.getForEntity("/api/v1/vets/999", String::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

}