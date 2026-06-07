package org.springframework.samples.petclinic.rest.mapper

import org.springframework.samples.petclinic.owner.Pet
import org.springframework.samples.petclinic.rest.dto.PetDto
import org.springframework.stereotype.Component

@Component
class PetMapper(
    private val visitMapper: VisitMapper,
    private val petTypeMapper: PetTypeMapper
) {

    fun toDto(pet: Pet): PetDto {
        return PetDto(
            id = pet.id,
            name = pet.name,
            birthDate = pet.birthDate,
            type = pet.type?.let { petTypeMapper.toDto(it) },
            visits = pet.getVisits().map { visitMapper.toDto(it) }
        )
    }

}