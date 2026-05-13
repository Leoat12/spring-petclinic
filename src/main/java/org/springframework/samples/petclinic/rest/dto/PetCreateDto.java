package org.springframework.samples.petclinic.rest.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PetCreateDto(@NotBlank String name, @NotNull LocalDate birthDate, @NotNull Integer typeId) {
}