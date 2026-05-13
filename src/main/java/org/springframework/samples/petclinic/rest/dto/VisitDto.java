package org.springframework.samples.petclinic.rest.dto;

import java.time.LocalDate;

public record VisitDto(Integer id, LocalDate date, String description) {
}