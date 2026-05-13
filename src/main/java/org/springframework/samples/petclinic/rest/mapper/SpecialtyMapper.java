package org.springframework.samples.petclinic.rest.mapper;

import org.mapstruct.Mapper;
import org.springframework.samples.petclinic.vet.Specialty;
import org.springframework.samples.petclinic.rest.dto.SpecialtyDto;

@Mapper(componentModel = "spring")
public interface SpecialtyMapper {

	SpecialtyDto toDto(Specialty specialty);

}