package org.springframework.samples.petclinic.owner

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledInNativeImage
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.text.ParseException
import java.util.Locale

@ExtendWith(MockitoExtension::class)
@DisabledInNativeImage
class PetTypeFormatterTests {

    @Mock
    private lateinit var types: PetTypeRepository

    private lateinit var petTypeFormatter: PetTypeFormatter

    @BeforeEach
    fun setup() {
        petTypeFormatter = PetTypeFormatter(types)
    }

    @Test
    fun testPrint() {
        val petType = PetType()
        petType.name = "Hamster"
        val petTypeName = petTypeFormatter.print(petType, Locale.ENGLISH)
        assertThat(petTypeName).isEqualTo("Hamster")
    }

    @Test
    fun shouldParse() {
        given(types.findPetTypes()).willReturn(makePetTypes())
        val petType = petTypeFormatter.parse("Bird", Locale.ENGLISH)
        assertThat(petType.name).isEqualTo("Bird")
    }

    @Test
    fun shouldThrowParseException() {
        given(types.findPetTypes()).willReturn(makePetTypes())
        org.junit.jupiter.api.Assertions.assertThrows(ParseException::class.java) {
            petTypeFormatter.parse("Fish", Locale.ENGLISH)
        }
    }

    private fun makePetTypes(): List<PetType> {
        val petTypes = mutableListOf<PetType>()
        petTypes.add(PetType().apply { name = "Dog" })
        petTypes.add(PetType().apply { name = "Bird" })
        return petTypes
    }

}