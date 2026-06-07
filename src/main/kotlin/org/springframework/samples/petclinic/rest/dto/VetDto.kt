package org.springframework.samples.petclinic.rest.dto

data class VetDto(
    val id: Int?,
    val firstName: String?,
    val lastName: String?,
    val specialties: List<SpecialtyDto>
)