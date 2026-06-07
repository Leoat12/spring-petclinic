package org.springframework.samples.petclinic.rest.mapper

import org.springframework.samples.petclinic.rest.dto.SpecialtyDto
import org.springframework.samples.petclinic.vet.Specialty
import org.springframework.stereotype.Component

@Component
class SpecialtyMapper {

    fun toDto(specialty: Specialty): SpecialtyDto {
        return SpecialtyDto(
            id = specialty.id,
            name = specialty.name
        )
    }

}