package org.springframework.samples.petclinic.rest.dto

import java.time.LocalDate

data class VisitDto(
    val id: Int?,
    val date: LocalDate?,
    val description: String?
)