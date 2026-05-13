package org.springframework.samples.petclinic.rest.dto;

import java.time.LocalDate;
import java.util.List;

public record PetDto(Integer id, String name, LocalDate birthDate, PetTypeDto type, List<VisitDto> visits) {
}