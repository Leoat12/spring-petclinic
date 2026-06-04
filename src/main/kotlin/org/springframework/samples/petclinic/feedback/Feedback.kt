package org.springframework.samples.petclinic.feedback

import java.time.LocalDateTime

import org.springframework.samples.petclinic.model.BaseEntity

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

open class Feedback : BaseEntity() {

	@NotBlank
	var name: String? = null

	@Email
	var email: String? = null

	@NotBlank
	@Size(max = 2000)
	var message: String? = null

	var createdAt: LocalDateTime = LocalDateTime.now()

}