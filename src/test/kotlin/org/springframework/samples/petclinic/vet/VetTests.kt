package org.springframework.samples.petclinic.vet

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class VetTests {

    private lateinit var vet: Vet

    @BeforeEach
    fun setUp() {
        vet = Vet()
        vet.firstName = "James"
        vet.lastName = "Carter"
    }

    @Test
    fun shouldAddSpecialty() {
        val specialty = Specialty()
        specialty.name = "radiology"
        vet.addSpecialty(specialty)

        assertThat(vet.getNrOfSpecialties()).isEqualTo(1)
        assertThat(vet.getSpecialties()).contains(specialty)
    }

    @Test
    fun shouldAddMultipleSpecialties() {
        val radiology = Specialty()
        radiology.name = "radiology"
        val surgery = Specialty()
        surgery.name = "surgery"
        vet.addSpecialty(radiology)
        vet.addSpecialty(surgery)

        assertThat(vet.getNrOfSpecialties()).isEqualTo(2)
        assertThat(vet.getSpecialties()).hasSize(2)
    }

    @Test
    fun shouldReturnZeroSpecialtiesWhenNone() {
        assertThat(vet.getNrOfSpecialties()).isZero()
        assertThat(vet.getSpecialties()).isEmpty()
    }

    @Test
    fun shouldReturnSpecialtiesSortedByName() {
        val surgery = Specialty()
        surgery.name = "surgery"
        val dentistry = Specialty()
        dentistry.name = "dentistry"

        vet.addSpecialty(surgery)
        vet.addSpecialty(dentistry)

        assertThat(vet.getSpecialties()[0].name).isEqualTo("dentistry")
        assertThat(vet.getSpecialties()[1].name).isEqualTo("surgery")
    }

    @Test
    fun shouldSetAndGetFirstName() {
        vet.firstName = "Helen"
        assertThat(vet.firstName).isEqualTo("Helen")
    }

    @Test
    fun shouldSetAndGetLastName() {
        vet.lastName = "Leary"
        assertThat(vet.lastName).isEqualTo("Leary")
    }

}