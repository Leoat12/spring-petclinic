package org.springframework.samples.petclinic.rest.dto

import jakarta.validation.constraints.NotBlank
import java.time.LocalDate

data class VisitCreateDto(
    val date: LocalDate?,
    @field:NotBlank val description: String
)