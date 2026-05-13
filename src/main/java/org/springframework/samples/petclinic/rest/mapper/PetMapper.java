package org.springframework.samples.petclinic.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.rest.dto.PetDto;

@Mapper(componentModel = "spring", uses = { VisitMapper.class, PetTypeMapper.class })
public interface PetMapper {

	@Mapping(source = "type", target = "type")
	PetDto toDto(Pet pet);

}