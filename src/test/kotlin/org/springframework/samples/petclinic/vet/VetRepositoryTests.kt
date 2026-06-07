package org.springframework.samples.petclinic.vet

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace
import org.springframework.context.annotation.Import
import org.springframework.data.domain.Pageable
import org.springframework.samples.petclinic.service.EntityUtils
import org.springframework.transaction.annotation.Transactional

@DataJdbcTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(JdbcClientVetRepository::class)
class VetRepositoryTests {

    @Autowired
    protected lateinit var vets: VetRepository

    @Test
    fun shouldFindAllVets() {
        val allVets = vets.findAll()
        assertThat(allVets).isNotEmpty()
    }

    @Test
    fun shouldFindVetsWithSpecialties() {
        val allVets = vets.findAll()
        val vet = EntityUtils.getById(allVets, Vet::class.java, 3)
        assertThat(vet.lastName).isEqualTo("Douglas")
        assertThat(vet.getNrOfSpecialties()).isEqualTo(2)
        assertThat(vet.getSpecialties()[0].name).isEqualTo("dentistry")
        assertThat(vet.getSpecialties()[1].name).isEqualTo("surgery")
    }

    @Test
    fun shouldFindAllVetsPaginated() {
        val page = vets.findAll(Pageable.ofSize(10))
        assertThat(page.totalElements).isGreaterThan(0)
        assertThat(page.content).isNotEmpty()
    }

    @Test
    fun shouldFindVetById() {
        val optionalVet = vets.findById(1)
        assertThat(optionalVet).isPresent
        assertThat(optionalVet.get().firstName).isEqualTo("James")
    }

    @Test
    fun shouldReturnEmptyForNonExistentVet() {
        val optionalVet = vets.findById(999)
        assertThat(optionalVet).isEmpty
    }

    @Test
    fun shouldFindVetCaching() {
        val firstCall = vets.findAll()
        val secondCall = vets.findAll()
        assertThat(secondCall).hasSameSizeAs(firstCall)
    }

    @Test
    fun shouldFindVetByIdWithSpecialties() {
        val optionalVet = vets.findById(3)
        assertThat(optionalVet).isPresent
        val vet = optionalVet.get()
        assertThat(vet.getNrOfSpecialties()).isGreaterThan(0)
        assertThat(vet.getSpecialties()).isNotEmpty()
        assertThat(vet.getSpecialties()[0].name).isNotNull()
    }

    @Test
    fun shouldLoadSpecialtiesForAllVets() {
        val allVets = vets.findAll()
        assertThat(allVets).isNotEmpty()
        for (vet in allVets) {
            if (vet.id == 3) {
                assertThat(vet.getNrOfSpecialties()).isEqualTo(2)
                assertThat(vet.getSpecialties()[0].id).isNotNull()
                assertThat(vet.getSpecialties()[1].id).isNotNull()
            }
        }
    }

    @Test
    fun shouldFindVetByIdWithCorrectSpecialties() {
        val vet1 = vets.findById(1)
        assertThat(vet1).isPresent
        assertThat(vet1.get().getSpecialties()).isEmpty()

        val vet3 = vets.findById(3)
        assertThat(vet3).isPresent
        assertThat(vet3.get().getSpecialties()).isNotEmpty()
        assertThat(vet3.get().getSpecialties()[0].name).isEqualTo("dentistry")
    }

}