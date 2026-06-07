package org.springframework.samples.petclinic.rest.controller

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
import org.springframework.test.annotation.DirtiesContext

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class VetRestControllerIntegrationTests {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun shouldListVets() {
        val response: ResponseEntity<PagedResultDto<Any>> = restTemplate.getForEntity("/api/v1/vets", PagedResultDto::class.java) as ResponseEntity<PagedResultDto<Any>>
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull()
        assertThat(response.body!!.totalElements).isGreaterThan(0)
    }

    @Test
    fun shouldGetVetById() {
        val response: ResponseEntity<VetDto> = restTemplate.getForEntity("/api/v1/vets/1", VetDto::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull()
        assertThat(response.body!!.firstName).isEqualTo("James")
    }

    @Test
    fun shouldReturnNotFoundForNonExistentVet() {
        val response: ResponseEntity<String> = restTemplate.getForEntity("/api/v1/vets/999", String::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

}