package org.springframework.samples.petclinic.rest.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class FeedbackCreateDto(
    @field:NotBlank val name: String,
    @field:Email val email: String?,
    @field:NotBlank @field:Size(max = 2000) val message: String
)