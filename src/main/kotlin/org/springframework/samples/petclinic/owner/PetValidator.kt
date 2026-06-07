package org.springframework.samples.petclinic.owner

import org.springframework.util.StringUtils
import org.springframework.validation.Errors
import org.springframework.validation.Validator

class PetValidator : Validator {

    companion object {
        private const val REQUIRED = "required"
    }

    override fun validate(obj: Any, errors: Errors) {
        val pet = obj as Pet
        val name = pet.name
        if (!StringUtils.hasText(name)) {
            errors.rejectValue("name", REQUIRED, REQUIRED)
        }

        if (pet.isNew() && pet.type == null) {
            errors.rejectValue("type", REQUIRED, REQUIRED)
        }

        if (pet.birthDate == null) {
            errors.rejectValue("birthDate", REQUIRED, REQUIRED)
        }
    }

    override fun supports(clazz: Class<*>): Boolean {
        return Pet::class.java.isAssignableFrom(clazz)
    }

}