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
import org.springframework.samples.petclinic.rest.dto.PetCreateDto
import org.springframework.samples.petclinic.rest.dto.PetDto
import org.springframework.samples.petclinic.rest.dto.VisitCreateDto
import org.springframework.samples.petclinic.rest.dto.VisitDto
import org.springframework.test.annotation.DirtiesContext
import java.time.LocalDate

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PetAndVisitApiContractTests {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun listPetsForOwner() {
        val response: ResponseEntity<Array<PetDto>> = restTemplate.getForEntity("/api/v1/owners/1/pets", Array<PetDto>::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull()
    }

    @Test
    fun getPetDetail() {
        val response: ResponseEntity<PetDto> = restTemplate.getForEntity("/api/v1/owners/1/pets/1", PetDto::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val pet = response.body
        assertThat(pet).isNotNull()
        assertThat(pet!!.id).isNotNull()
        assertThat(pet.name).isNotNull()
        assertThat(pet.birthDate).isNotNull()
        assertThat(pet.type).isNotNull()
    }

    @Test
    fun createPetForOwner() {
        val dto = PetCreateDto("NewDog", LocalDate.of(2023, 6, 15), 1)
        val response: ResponseEntity<PetDto> = restTemplate.postForEntity("/api/v1/owners/6/pets", dto, PetDto::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        assertThat(response.body).isNotNull()
        assertThat(response.body!!.name).isEqualTo("NewDog")
    }

    @Test
    fun createPetValidatesRequiredFields() {
        val blankDto = mapOf("name" to "", "birthDate" to null, "typeId" to null)
        val response: ResponseEntity<String> = restTemplate.postForEntity("/api/v1/owners/6/pets", blankDto, String::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun updatePetForOwner() {
        val dto = PetCreateDto("UpdatedName", LocalDate.of(2020, 1, 1), 1)
        val response: ResponseEntity<PetDto> = restTemplate.exchange(
            "/api/v1/owners/6/pets/7", HttpMethod.PUT, HttpEntity(dto), PetDto::class.java
        )
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull()
        assertThat(response.body!!.name).isEqualTo("UpdatedName")
    }

    @Test
    fun listVisitsForPet() {
        val response: ResponseEntity<Array<VisitDto>> = restTemplate.getForEntity(
            "/api/v1/owners/6/pets/7/visits", Array<VisitDto>::class.java
        )
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull()
    }

    @Test
    fun createVisitForPet() {
        val dto = VisitCreateDto(LocalDate.of(2024, 12, 1), "Annual checkup")
        val response: ResponseEntity<VisitDto> = restTemplate.postForEntity(
            "/api/v1/owners/6/pets/7/visits", dto, VisitDto::class.java
        )
        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        assertThat(response.body).isNotNull()
        assertThat(response.body!!.description).isEqualTo("Annual checkup")
    }

    @Test
    fun createVisitValidatesDescriptionNotBlank() {
        val blankDto = VisitCreateDto(LocalDate.of(2024, 1, 1), "")
        val response: ResponseEntity<String> = restTemplate.postForEntity(
            "/api/v1/owners/6/pets/7/visits", blankDto, String::class.java
        )
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun getPetForNonExistentOwnerReturnsNotFound() {
        val response: ResponseEntity<String> = restTemplate.getForEntity("/api/v1/owners/999/pets", String::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun getVisitForNonExistentPetReturnsNotFound() {
        val response: ResponseEntity<String> = restTemplate.getForEntity("/api/v1/owners/6/pets/999/visits", String::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

}