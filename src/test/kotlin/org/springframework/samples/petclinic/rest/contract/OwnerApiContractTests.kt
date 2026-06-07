package org.springframework.samples.petclinic.rest.contract

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
class OwnerApiContractTests {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun listOwnersReturnsPagedResult() {
        val response: ResponseEntity<PagedResultDto<Any>> = restTemplate.getForEntity("/api/v1/owners", PagedResultDto::class.java) as ResponseEntity<PagedResultDto<Any>>
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull()
        assertThat(response.body!!.content).isNotNull()
        assertThat(response.body!!.pageNumber).isGreaterThan(0)
        assertThat(response.body!!.pageSize).isGreaterThan(0)
        assertThat(response.body!!.totalElements).isGreaterThan(0)
        assertThat(response.body!!.totalPages).isGreaterThan(0)
    }

    @Test
    fun listOwnersSupportsPagination() {
        val page1: ResponseEntity<PagedResultDto<Any>> = restTemplate.getForEntity("/api/v1/owners?page=1&size=2", PagedResultDto::class.java) as ResponseEntity<PagedResultDto<Any>>
        assertThat(page1.body).isNotNull()
        assertThat(page1.body!!.content.size).isLessThanOrEqualTo(2)

        val page2: ResponseEntity<PagedResultDto<Any>> = restTemplate.getForEntity("/api/v1/owners?page=2&size=2", PagedResultDto::class.java) as ResponseEntity<PagedResultDto<Any>>
        assertThat(page2.body).isNotNull()
    }

    @Test
    fun getOwnerReturnsCompleteDto() {
        val response: ResponseEntity<OwnerDto> = restTemplate.getForEntity("/api/v1/owners/1", OwnerDto::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val owner = response.body
        assertThat(owner).isNotNull()
        assertThat(owner!!.id).isNotNull()
        assertThat(owner.firstName).isNotNull()
        assertThat(owner.lastName).isNotNull()
        assertThat(owner.address).isNotNull()
        assertThat(owner.city).isNotNull()
        assertThat(owner.telephone).isNotNull()
    }

    @Test
    fun createOwnerReturnsCreatedWithLocation() {
        val dto = OwnerCreateDto("Jane", "Doe", "456 Oak Ave", "Portland", "5035551234", "jane@doe.com")
        val response: ResponseEntity<OwnerDto> = restTemplate.postForEntity("/api/v1/owners", dto, OwnerDto::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        assertThat(response.body).isNotNull()
        assertThat(response.body!!.id).isNotNull()
        assertThat(response.body!!.firstName).isEqualTo("Jane")
        assertThat(response.body!!.lastName).isEqualTo("Doe")
    }

    @Test
    fun createOwnerValidatesAllFields() {
        val blankDto = OwnerCreateDto("", "", "", "", "short", "invalid")
        val response: ResponseEntity<String> = restTemplate.postForEntity("/api/v1/owners", blankDto, String::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun createOwnerWithEmail() {
        val dto = OwnerCreateDto("Jane", "Doe", "456 Oak Ave", "Portland", "5035551234", "jane@example.com")
        val response: ResponseEntity<OwnerDto> = restTemplate.postForEntity("/api/v1/owners", dto, OwnerDto::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        assertThat(response.body).isNotNull()
        assertThat(response.body!!.email).isEqualTo("jane@example.com")
    }

    @Test
    fun createOwnerWithoutEmail() {
        val dto = OwnerCreateDto("John", "Smith", "789 Pine St", "Seattle", "2065551234", null)
        val response: ResponseEntity<OwnerDto> = restTemplate.postForEntity("/api/v1/owners", dto, OwnerDto::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        assertThat(response.body).isNotNull()
        assertThat(response.body!!.email).isNull()
    }

    @Test
    fun updateOwnerModifiesExistingOwner() {
        val dto = OwnerUpdateDto("UpdatedName", "Franklin", "110 W. Liberty St.", "Madison", "6085551023", "updated@email.com")
        val response: ResponseEntity<OwnerDto> = restTemplate.exchange(
            "/api/v1/owners/1", HttpMethod.PUT, HttpEntity(dto), OwnerDto::class.java
        )
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body!!.firstName).isEqualTo("UpdatedName")
    }

    @Test
    fun deleteOwnerRemovesOwner() {
        val dto = OwnerCreateDto("Delete", "Owner", "789 Pine", "Town", "9998887776", null)
        val createResponse: ResponseEntity<OwnerDto> = restTemplate.postForEntity("/api/v1/owners", dto, OwnerDto::class.java)
        val id = createResponse.body!!.id

        val deleteResponse: ResponseEntity<Void> = restTemplate.exchange(
            "/api/v1/owners/$id", HttpMethod.DELETE, null, Void::class.java
        )
        assertThat(deleteResponse.statusCode).isEqualTo(HttpStatus.NO_CONTENT)

        val getResponse: ResponseEntity<String> = restTemplate.getForEntity("/api/v1/owners/$id", String::class.java)
        assertThat(getResponse.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

}