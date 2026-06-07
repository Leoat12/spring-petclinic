package org.springframework.samples.petclinic.rest.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class PetCreateDto(
    @field:NotBlank val name: String,
    @field:NotNull val birthDate: LocalDate,
    @field:NotNull val typeId: Int
)