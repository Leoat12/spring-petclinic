package org.springframework.samples.petclinic.rest.mapper

import org.springframework.samples.petclinic.owner.PetType
import org.springframework.samples.petclinic.rest.dto.PetTypeDto
import org.springframework.stereotype.Component

@Component
class PetTypeMapper {

    fun toDto(petType: PetType): PetTypeDto {
        return PetTypeDto(
            id = petType.id,
            name = petType.name
        )
    }

}