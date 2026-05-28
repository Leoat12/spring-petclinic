package org.springframework.samples.petclinic.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record OwnerCreateDto(@NotBlank String firstName, @NotBlank String lastName, @NotBlank String address,
		@NotBlank String city,
		@NotBlank @Pattern(regexp = "\\d{10}", message = "Telephone must be 10 digits") String telephone,
		@Email String email) {
}