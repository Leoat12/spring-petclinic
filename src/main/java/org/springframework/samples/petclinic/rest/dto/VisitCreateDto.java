package org.springframework.samples.petclinic.rest.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;

public record VisitCreateDto(LocalDate date, @NotBlank String description) {
}