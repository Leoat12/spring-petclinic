package org.springframework.samples.petclinic.rest.mapper;

import org.mapstruct.Mapper;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.rest.dto.PetTypeDto;

@Mapper(componentModel = "spring")
public interface PetTypeMapper {

	PetTypeDto toDto(PetType petType);

}