package org.springframework.samples.petclinic.rest.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.TestRestTemplate
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class GlobalExceptionHandlerTests {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun shouldReturnNotFoundResponseForResourceNotFoundException() {
        val response: ResponseEntity<String> = restTemplate.getForEntity("/api/v1/owners/999", String::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        assertThat(response.body).isNotNull()
        assertThat(response.headers.contentType).isNotNull()
    }

    @Test
    fun shouldReturnValidationErrorForInvalidInput() {
        val json = """{"firstName":"","lastName":"","address":"","city":"","telephone":"abc"}"""
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(json, headers)

        val response: ResponseEntity<String> = restTemplate.postForEntity("/api/v1/owners", entity, String::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.body).isNotBlank()
        assertThat(response.body).contains("firstName")
    }

    @Test
    fun shouldReturnNotFoundForInvalidUrl() {
        val response: ResponseEntity<String> = restTemplate.getForEntity("/api/v1/nonexistent", String::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

}