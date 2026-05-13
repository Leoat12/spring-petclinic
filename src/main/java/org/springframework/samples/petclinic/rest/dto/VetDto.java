package org.springframework.samples.petclinic.rest.dto;

import java.util.List;

public record VetDto(Integer id, String firstName, String lastName, List<SpecialtyDto> specialties) {
}