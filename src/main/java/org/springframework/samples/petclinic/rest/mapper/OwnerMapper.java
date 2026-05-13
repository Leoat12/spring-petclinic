package org.springframework.samples.petclinic.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.rest.dto.OwnerCreateDto;
import org.springframework.samples.petclinic.rest.dto.OwnerDto;
import org.springframework.samples.petclinic.rest.dto.OwnerUpdateDto;

@Mapper(componentModel = "spring", uses = { PetMapper.class })
public interface OwnerMapper {

	OwnerDto toDto(Owner owner);

	List<OwnerDto> toDtoList(List<Owner> owners);

	Owner toEntity(OwnerCreateDto dto);

	void updateEntity(OwnerUpdateDto dto, @MappingTarget Owner owner);

}