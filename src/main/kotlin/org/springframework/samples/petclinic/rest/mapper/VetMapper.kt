package org.springframework.samples.petclinic.rest.mapper

import org.springframework.samples.petclinic.rest.dto.VetDto
import org.springframework.samples.petclinic.vet.Vet
import org.springframework.stereotype.Component

@Component
class VetMapper(
    private val specialtyMapper: SpecialtyMapper
) {

    fun toDto(vet: Vet): VetDto {
        return VetDto(
            id = vet.id,
            firstName = vet.firstName,
            lastName = vet.lastName,
            specialties = vet.getSpecialties().map { specialtyMapper.toDto(it) }
        )
    }

}