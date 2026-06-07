package org.springframework.samples.petclinic.rest.dto

import java.time.LocalDate

data class PetDto(
    val id: Int?,
    val name: String?,
    val birthDate: LocalDate?,
    val type: PetTypeDto?,
    val visits: List<VisitDto>
)