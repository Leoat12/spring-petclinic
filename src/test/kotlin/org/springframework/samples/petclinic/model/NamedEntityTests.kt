package org.springframework.samples.petclinic.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.samples.petclinic.owner.PetType

class NamedEntityTests {

    @Test
    fun shouldReturnNameInToString() {
        val petType = PetType()
        petType.name = "cat"
        assertThat(petType.toString()).isEqualTo("cat")
    }

    @Test
    fun shouldReturnNullPlaceholderInToStringWhenNameIsNull() {
        val petType = PetType()
        assertThat(petType.toString()).isEqualTo("<null>")
    }

}