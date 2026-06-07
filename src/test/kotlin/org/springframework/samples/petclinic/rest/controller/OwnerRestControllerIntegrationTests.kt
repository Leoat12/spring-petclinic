package org.springframework.samples.petclinic.rest.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.TestRestTemplate
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.samples.petclinic.rest.dto.OwnerCreateDto
import org.springframework.samples.petclinic.rest.dto.OwnerDto
import org.springframework.samples.petclinic.rest.dto.OwnerUpdateDto
import org.springframework.samples.petclinic.rest.dto.PagedResultDto
import org.springframework.test.annotation.DirtiesContext

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class OwnerRestControllerIntegrationTests {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun shouldListOwners() {
        val response: ResponseEntity<PagedResultDto<Any>> = restTemplate.getForEntity("/api/v1/owners", PagedResultDto::class.java) as ResponseEntity<PagedResultDto<Any>>
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull()
        assertThat(response.body!!.totalElements).isGreaterThan(0)
    }

    @Test
    fun shouldGetOwnerById() {
        val response: ResponseEntity<OwnerDto> = restTemplate.getForEntity("/api/v1/owners/1", OwnerDto::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull()
        assertThat(response.body!!.firstName).isNotNull()
    }

    @Test
    fun shouldReturnNotFoundForNonExistentOwner() {
        val response: ResponseEntity<String> = restTemplate.getForEntity("/api/v1/owners/999", String::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun shouldCreateOwner() {
        val dto = OwnerCreateDto("John", "Doe", "123 Main St", "Springfield", "5551234567", "john@doe.com")
        val response: ResponseEntity<OwnerDto> = restTemplate.postForEntity("/api/v1/owners", dto, OwnerDto::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        assertThat(response.body).isNotNull()
        assertThat(response.body!!.firstName).isEqualTo("John")
        assertThat(response.body!!.id).isNotNull()
    }

    @Test
    fun shouldRejectCreateOwnerWithInvalidData() {
        val dto = OwnerCreateDto("", "", "", "", "abc", "invalid-email")
        val response: ResponseEntity<String> = restTemplate.postForEntity("/api/v1/owners", dto, String::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun shouldUpdateOwner() {
        val dto = OwnerUpdateDto("UpdatedName", "Franklin", "110 W. Liberty St.", "Madison", "6085551023", "updated@email.com")
        val response: ResponseEntity<OwnerDto> = restTemplate.exchange(
            "/api/v1/owners/1", HttpMethod.PUT, HttpEntity(dto), OwnerDto::class.java
        )
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull()
        assertThat(response.body!!.firstName).isEqualTo("UpdatedName")
    }

    @Test
    fun shouldReturnNotFoundWhenUpdatingNonExistentOwner() {
        val dto = OwnerUpdateDto("Updated", "Franklin", "110 W. Liberty St.", "Madison", "6085551023", null)
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/api/v1/owners/999", HttpMethod.PUT, HttpEntity(dto), String::class.java
        )
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun shouldDeleteOwner() {
        val dto = OwnerCreateDto("Delete", "Me", "123 St", "City", "1234567890", null)
        val createResponse: ResponseEntity<OwnerDto> = restTemplate.postForEntity("/api/v1/owners", dto, OwnerDto::class.java)
        val id = createResponse.body!!.id

        val deleteResponse: ResponseEntity<Void> = restTemplate.exchange(
            "/api/v1/owners/$id", HttpMethod.DELETE, null, Void::class.java
        )
        assertThat(deleteResponse.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
    }

    @Test
    fun shouldReturnNotFoundWhenDeletingNonExistentOwner() {
        val deleteResponse: ResponseEntity<String> = restTemplate.exchange(
            "/api/v1/owners/999", HttpMethod.DELETE, null, String::class.java
        )
        assertThat(deleteResponse.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun shouldCreateOwnerWithEmail() {
        val dto = OwnerCreateDto("Email", "Test", "789 Elm St", "Boston", "6175551234", "email@test.com")
        val response: ResponseEntity<OwnerDto> = restTemplate.postForEntity("/api/v1/owners", dto, OwnerDto::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        assertThat(response.body).isNotNull()
        assertThat(response.body!!.email).isEqualTo("email@test.com")
    }

    @Test
    fun shouldCreateOwnerWithoutEmail() {
        val dto = OwnerCreateDto("No", "Email", "456 Pine St", "Chicago", "3125551234", null)
        val response: ResponseEntity<OwnerDto> = restTemplate.postForEntity("/api/v1/owners", dto, OwnerDto::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        assertThat(response.body).isNotNull()
        assertThat(response.body!!.email).isNull()
    }

    @Test
    fun shouldUpdateOwnerEmail() {
        val dto = OwnerUpdateDto("George", "Franklin", "110 W. Liberty St.", "Madison", "6085551023", "new@email.com")
        val response: ResponseEntity<OwnerDto> = restTemplate.exchange(
            "/api/v1/owners/1", HttpMethod.PUT, HttpEntity(dto), OwnerDto::class.java
        )
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull()
        assertThat(response.body!!.email).isEqualTo("new@email.com")
    }

    @Test
    fun shouldRejectCreateOwnerWithInvalidEmail() {
        val dto = OwnerCreateDto("Bad", "Email", "123 St", "City", "1234567890", "not-an-email")
        val response: ResponseEntity<String> = restTemplate.postForEntity("/api/v1/owners", dto, String::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

}