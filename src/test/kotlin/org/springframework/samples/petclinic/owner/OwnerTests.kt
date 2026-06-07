package org.springframework.samples.petclinic.owner

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class OwnerTests {

    private lateinit var owner: Owner
    private lateinit var dog: PetType

    @BeforeEach
    fun setUp() {
        owner = Owner()
        owner.firstName = "George"
        owner.lastName = "Franklin"
        owner.address = "110 W. Liberty St."
        owner.city = "Madison"
        owner.telephone = "6085551023"

        dog = PetType()
        dog.id = 1
        dog.name = "dog"
    }

    @Test
    fun shouldAddNewPet() {
        val pet = Pet()
        pet.name = "Fido"
        pet.birthDate = LocalDate.of(2023, 1, 1)
        pet.type = dog

        owner.addPet(pet)

        assertThat(owner.getPets()).contains(pet)
        assertThat(owner.getPet("Fido")).isNotNull()
    }

    @Test
    fun shouldNotAddPetWithExistingId() {
        val pet = Pet()
        pet.id = 1
        pet.name = "Fido"

        owner.addPet(pet)

        assertThat(owner.getPets()).isEmpty()
    }

    @Test
    fun shouldGetPetByNameCaseInsensitive() {
        val pet = Pet()
        pet.name = "Fido"
        pet.birthDate = LocalDate.of(2023, 1, 1)
        pet.type = dog
        owner.addPet(pet)

        assertThat(owner.getPet("Fido")).isNotNull()
        assertThat(owner.getPet("fido")).isNotNull()
    }

    @Test
    fun shouldGetPetById() {
        val pet = Pet()
        pet.name = "Fido"
        pet.birthDate = LocalDate.of(2023, 1, 1)
        pet.type = dog
        owner.addPet(pet)
        pet.id = 7

        assertThat(owner.getPet(7)).isNotNull()
        assertThat(owner.getPet(999)).isNull()
    }

    @Test
    fun shouldReturnNullForNonExistentPet() {
        assertThat(owner.getPet("NonExistent")).isNull()
        assertThat(owner.getPet(999)).isNull()
    }

    @Test
    fun shouldAddVisitToPet() {
        val pet = Pet()
        pet.name = "Fido"
        pet.birthDate = LocalDate.of(2023, 1, 1)
        pet.type = dog
        owner.addPet(pet)
        pet.id = 7

        val visit = Visit()
        visit.date = LocalDate.of(2024, 1, 15)
        visit.description = "rabies shot"

        owner.addVisit(7, visit)

        assertThat(pet.getVisits()).contains(visit)
    }

    @Test
    fun shouldThrowWhenAddingVisitToNonExistentPet() {
        val visit = Visit()
        visit.description = "checkup"

        assertThatIllegalArgumentException().isThrownBy { owner.addVisit(999, visit) }
    }

    @Test
    fun shouldThrowWhenVisitIsNull() {
        assertThatIllegalArgumentException().isThrownBy { owner.addVisit(1, null) }
    }

    @Test
    fun shouldThrowWhenPetIdIsNull() {
        assertThatIllegalArgumentException().isThrownBy { owner.addVisit(null, Visit()) }
    }

    @Test
    fun shouldSetProperties() {
        owner.address = "New Address"
        assertThat(owner.address).isEqualTo("New Address")

        owner.city = "New City"
        assertThat(owner.city).isEqualTo("New City")

        owner.telephone = "9999999999"
        assertThat(owner.telephone).isEqualTo("9999999999")

        owner.email = "test@example.com"
        assertThat(owner.email).isEqualTo("test@example.com")

        owner.email = null
        assertThat(owner.email).isNull()
    }

    @Test
    fun shouldReturnStringRepresentation() {
        owner.firstName = "George"
        owner.lastName = "Franklin"
        owner.address = "110 W. Liberty St."
        owner.city = "Madison"
        owner.telephone = "6085551023"
        owner.email = "george@franklin.com"
        val result = owner.toString()
        assertThat(result).contains("Franklin")
        assertThat(result).contains("George")
        assertThat(result).contains("george@franklin.com")
        assertThat(result).isNotEmpty()
    }

    @Test
    fun shouldThrowWhenAddVisitWithNullPetId() {
        assertThatIllegalArgumentException().isThrownBy { owner.addVisit(null, Visit()) }
            .withMessageContaining("Pet identifier must not be null")
    }

    @Test
    fun shouldThrowWhenAddVisitWithNullVisit() {
        assertThatIllegalArgumentException().isThrownBy { owner.addVisit(1, null) }
            .withMessageContaining("Visit must not be null")
    }

    @Test
    fun shouldGetPetByNameIgnoringNewPets() {
        val newPet = Pet()
        newPet.name = "NewFido"
        newPet.birthDate = LocalDate.of(2023, 1, 1)
        newPet.type = dog
        owner.addPet(newPet)

        val existingPet = Pet()
        existingPet.name = "OldFido"
        existingPet.birthDate = LocalDate.of(2023, 1, 1)
        existingPet.type = dog
        owner.addPet(existingPet)
        existingPet.id = 5

        assertThat(owner.getPet("NewFido", true)).isNull()
        assertThat(owner.getPet("NewFido", false)).isNotNull()
        assertThat(owner.getPet("OldFido", true)).isNotNull()
    }

}