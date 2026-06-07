package org.springframework.samples.petclinic.rest.mapper

import org.springframework.samples.petclinic.owner.Owner
import org.springframework.samples.petclinic.rest.dto.OwnerCreateDto
import org.springframework.samples.petclinic.rest.dto.OwnerDto
import org.springframework.samples.petclinic.rest.dto.OwnerUpdateDto
import org.springframework.stereotype.Component

@Component
class OwnerMapper(
    private val petMapper: PetMapper
) {

    fun toDto(owner: Owner): OwnerDto {
        return OwnerDto(
            id = owner.id,
            firstName = owner.firstName,
            lastName = owner.lastName,
            address = owner.address,
            city = owner.city,
            telephone = owner.telephone,
            email = owner.email,
            pets = owner.getPets().map { petMapper.toDto(it) }
        )
    }

    fun toDtoList(owners: List<Owner>): List<OwnerDto> {
        return owners.map { toDto(it) }
    }

    fun toEntity(dto: OwnerCreateDto): Owner {
        val owner = Owner()
        owner.firstName = dto.firstName
        owner.lastName = dto.lastName
        owner.address = dto.address
        owner.city = dto.city
        owner.telephone = dto.telephone
        owner.email = dto.email
        return owner
    }

    fun updateEntity(dto: OwnerUpdateDto, owner: Owner) {
        owner.firstName = dto.firstName
        owner.lastName = dto.lastName
        owner.address = dto.address
        owner.city = dto.city
        owner.telephone = dto.telephone
        owner.email = dto.email
    }

}