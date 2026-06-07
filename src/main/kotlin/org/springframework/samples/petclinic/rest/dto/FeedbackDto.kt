package org.springframework.samples.petclinic.rest.dto

import java.time.LocalDateTime

data class FeedbackDto(
    val id: Int?,
    val name: String?,
    val email: String?,
    val message: String?,
    val createdAt: LocalDateTime?
)