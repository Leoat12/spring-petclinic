package org.springframework.samples.petclinic.owner

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class PetTests {

    private lateinit var pet: Pet
    private lateinit var dog: PetType

    @BeforeEach
    fun setUp() {
        pet = Pet()
        pet.name = "Fido"
        pet.birthDate = LocalDate.of(2023, 1, 1)
        dog = PetType()
        dog.id = 1
        dog.name = "dog"
        pet.type = dog
    }

    @Test
    fun shouldSetAndGetName() {
        pet.name = "Buddy"
        assertThat(pet.name).isEqualTo("Buddy")
    }

    @Test
    fun shouldSetAndGetBirthDate() {
        val date = LocalDate.of(2022, 6, 15)
        pet.birthDate = date
        assertThat(pet.birthDate).isEqualTo(date)
    }

    @Test
    fun shouldSetAndGetType() {
        val cat = PetType()
        cat.name = "cat"
        pet.type = cat
        assertThat(pet.type!!.name).isEqualTo("cat")
    }

    @Test
    fun shouldAddVisit() {
        val visit = Visit()
        visit.description = "rabies shot"
        visit.date = LocalDate.of(2024, 1, 15)

        pet.addVisit(visit)

        assertThat(pet.getVisits()).contains(visit)
    }

    @Test
    fun shouldAddMultipleVisits() {
        val visit1 = Visit()
        visit1.description = "rabies shot"
        visit1.date = LocalDate.of(2024, 1, 15)

        val visit2 = Visit()
        visit2.description = "checkup"
        visit2.date = LocalDate.of(2024, 6, 1)

        pet.addVisit(visit1)
        pet.addVisit(visit2)

        assertThat(pet.getVisits()).hasSize(2)
        assertThat(pet.getVisits()).containsExactly(visit1, visit2)
    }

    @Test
    fun shouldBeNewWhenIdIsNull() {
        assertThat(pet.isNew()).isTrue()
    }

    @Test
    fun shouldNotBeNewWhenIdIsSet() {
        pet.id = 1
        assertThat(pet.isNew()).isFalse()
    }

}