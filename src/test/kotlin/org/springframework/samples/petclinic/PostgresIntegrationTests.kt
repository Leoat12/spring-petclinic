package org.springframework.samples.petclinic

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledInNativeImage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.TestRestTemplate
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.samples.petclinic.owner.Owner
import org.springframework.samples.petclinic.owner.OwnerRepository
import org.springframework.samples.petclinic.owner.PetType
import org.springframework.samples.petclinic.owner.PetTypeRepository
import org.springframework.samples.petclinic.testcontainers.BasePostgresIntegrationTest
import org.springframework.samples.petclinic.vet.Vet
import org.springframework.samples.petclinic.vet.VetRepository
import org.junit.jupiter.api.Assumptions.assumeTrue
import java.util.Optional

@DisabledInNativeImage
class PostgresIntegrationTests : BasePostgresIntegrationTest() {

    @Autowired
    private lateinit var vets: VetRepository

    @Autowired
    private lateinit var ownerRepository: OwnerRepository

    @Autowired
    private lateinit var petTypeRepository: PetTypeRepository

    @Autowired
    private lateinit var rest: TestRestTemplate

    @Test
    fun shouldFindAllVets() {
        assertThat(vets.findAll()).isNotEmpty()
    }

    @Test
    fun shouldFindVetById() {
        val vet = vets.findById(1)
        assertThat(vet).isPresent
        assertThat(vet.get().firstName).isEqualTo("James")
    }

    @Test
    fun shouldFindOwnersByLastName() {
        val results = ownerRepository.findByLastNameStartingWith("Davis", Pageable.unpaged())
        assertThat(results).hasSize(2)
    }

    @Test
    fun shouldFindOwnerById() {
        val owner = ownerRepository.findById(1)
        assertThat(owner).isPresent
        assertThat(owner.get().firstName).isEqualTo("George")
    }

    @Test
    fun shouldFindAllPetTypes() {
        assertThat(petTypeRepository.findPetTypes()).isNotEmpty()
    }

    @Test
    fun ownerDetails() {
        val result: ResponseEntity<String> = rest.exchange(RequestEntity.get("/owners/1").build(), String::class.java)
        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun ownerList() {
        val result: ResponseEntity<String> = rest.exchange(RequestEntity.get("/owners?lastName=").build(), String::class.java)
        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
    }

}