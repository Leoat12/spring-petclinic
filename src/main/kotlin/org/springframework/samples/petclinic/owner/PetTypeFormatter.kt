package org.springframework.samples.petclinic.owner

import org.springframework.format.Formatter
import org.springframework.stereotype.Component
import java.text.ParseException
import java.util.Locale

@Component
class PetTypeFormatter(
    private val types: PetTypeRepository
) : Formatter<PetType> {

    override fun print(petType: PetType, locale: Locale): String {
        val name = petType.name
        return name ?: "<null>"
    }

    override fun parse(text: String, locale: Locale): PetType {
        val findPetTypes = types.findPetTypes()
        for (type in findPetTypes) {
            if (type.name == text) {
                return type
            }
        }
        throw ParseException("type not found: $text", 0)
    }

}