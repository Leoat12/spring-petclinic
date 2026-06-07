package org.springframework.samples.petclinic.rest.dto

data class OwnerDto(
    val id: Int?,
    val firstName: String?,
    val lastName: String?,
    val address: String?,
    val city: String?,
    val telephone: String?,
    val email: String?,
    val pets: List<PetDto>
)