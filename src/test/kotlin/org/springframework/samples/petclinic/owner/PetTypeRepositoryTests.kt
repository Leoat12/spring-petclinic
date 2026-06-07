package org.springframework.samples.petclinic.owner

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace
import org.springframework.context.annotation.Import
import org.springframework.samples.petclinic.service.EntityUtils
import org.springframework.transaction.annotation.Transactional

@DataJdbcTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(JdbcClientPetTypeRepository::class)
class PetTypeRepositoryTests {

    @Autowired
    protected lateinit var petTypes: PetTypeRepository

    @Test
    fun shouldFindAllPetTypes() {
        val types = petTypes.findPetTypes()

        val petType1 = EntityUtils.getById(types, PetType::class.java, 1)
        assertThat(petType1.name).isEqualTo("cat")
        val petType4 = EntityUtils.getById(types, PetType::class.java, 4)
        assertThat(petType4.name).isEqualTo("snake")
    }

    @Test
    fun shouldFindPetTypesOrderedByName() {
        val types = petTypes.findPetTypes()
        assertThat(types).isSortedAccordingTo { a, b -> a.name!!.compareTo(b.name!!, ignoreCase = true) }
    }

    @Test
    fun shouldFindPetTypeById() {
        val petType = petTypes.findById(1)
        assertThat(petType).isPresent
        assertThat(petType.get().name).isEqualTo("cat")
    }

    @Test
    fun shouldReturnEmptyForNonExistentPetType() {
        val petType = petTypes.findById(999)
        assertThat(petType).isEmpty
    }

    @Test
    @Transactional
    fun shouldInsertPetType() {
        val petType = PetType()
        petType.name = "hamster"
        val saved = petTypes.save(petType)
        assertThat(saved).isNotNull()
        assertThat(petType.id).isNotNull()

        val types = petTypes.findPetTypes()
        assertThat(types.stream().anyMatch { t -> "hamster" == t.name }).isTrue()
    }

    @Test
    @Transactional
    fun shouldUpdatePetType() {
        val petType = petTypes.findById(1).orElseThrow()
        petType.name = "feline"
        petTypes.save(petType)

        val updated = petTypes.findById(1).orElseThrow()
        assertThat(updated.name).isEqualTo("feline")
    }

    @Test
    @Transactional
    fun shouldDeletePetType() {
        val petType = PetType()
        petType.name = "ferret"
        petTypes.save(petType)
        val id = petType.id!!

        petTypes.deleteById(id)
        assertThat(petTypes.findById(id)).isEmpty
    }

}