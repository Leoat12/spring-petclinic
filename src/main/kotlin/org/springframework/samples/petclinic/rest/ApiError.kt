package org.springframework.samples.petclinic.rest

data class ApiError(
    val status: Int,
    val error: String,
    val message: String?,
    val path: String,
    val timestamp: java.time.LocalDateTime
)