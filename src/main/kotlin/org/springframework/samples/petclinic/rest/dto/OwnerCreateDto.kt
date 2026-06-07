package org.springframework.samples.petclinic.rest.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class OwnerCreateDto(
    @field:NotBlank val firstName: String,
    @field:NotBlank val lastName: String,
    @field:NotBlank val address: String,
    @field:NotBlank val city: String,
    @field:NotBlank @field:Pattern(regexp = "\\d{10}", message = "Telephone must be 10 digits") val telephone: String,
    @field:Email val email: String?
)