package org.springframework.samples.petclinic.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FeedbackCreateDto(@NotBlank String name, @Email String email,
		@NotBlank @Size(max = 2000) String message) {
}