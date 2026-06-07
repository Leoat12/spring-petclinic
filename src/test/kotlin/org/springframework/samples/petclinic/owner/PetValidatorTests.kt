package org.springframework.samples.petclinic.owner

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledInNativeImage
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.validation.MapBindingResult
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
@DisabledInNativeImage
class PetValidatorTests {

    private lateinit var petValidator: PetValidator
    private lateinit var pet: Pet
    private lateinit var petType: PetType
    private lateinit var errors: MapBindingResult

    companion object {
        private const val petName = "Buddy"
        private const val petTypeName = "Dog"
        private val petBirthDate = LocalDate.of(1990, 1, 1)
    }

    @BeforeEach
    fun setUp() {
        petValidator = PetValidator()
        pet = Pet()
        petType = PetType()
        errors = MapBindingResult(hashMapOf<String, Any>(), "pet")
    }

    @Test
    fun validate() {
        petType.name = petTypeName
        pet.name = petName
        pet.type = petType
        pet.birthDate = petBirthDate

        petValidator.validate(pet, errors)

        assertFalse(errors.hasErrors())
    }

    @Nested
    inner class ValidateHasErrors {

        @Test
        fun validateWithInvalidPetName() {
            petType.name = petTypeName
            pet.name = ""
            pet.type = petType
            pet.birthDate = petBirthDate

            petValidator.validate(pet, errors)

            assertTrue(errors.hasFieldErrors("name"))
        }

        @Test
        fun validateWithInvalidPetType() {
            pet.name = petName
            pet.type = null
            pet.birthDate = petBirthDate

            petValidator.validate(pet, errors)

            assertTrue(errors.hasFieldErrors("type"))
        }

        @Test
        fun validateWithInvalidBirthDate() {
            petType.name = petTypeName
            pet.name = petName
            pet.type = petType
            pet.birthDate = null

            petValidator.validate(pet, errors)

            assertTrue(errors.hasFieldErrors("birthDate"))
        }

    }

    @Test
    fun shouldSupportPetClass() {
        assertTrue(petValidator.supports(Pet::class.java))
    }

    @Test
    fun shouldNotSupportNonPetClass() {
        assertFalse(petValidator.supports(Any::class.java))
        assertFalse(petValidator.supports(String::class.java))
    }

}