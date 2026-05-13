package org.springframework.samples.petclinic.rest.mapper;

import org.mapstruct.Mapper;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.rest.dto.VetDto;

@Mapper(componentModel = "spring", uses = { SpecialtyMapper.class })
public interface VetMapper {

	VetDto toDto(Vet vet);

}