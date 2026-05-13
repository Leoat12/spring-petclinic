package org.springframework.samples.petclinic.rest.mapper;

import org.mapstruct.Mapper;
import org.springframework.samples.petclinic.owner.Visit;
import org.springframework.samples.petclinic.rest.dto.VisitDto;

@Mapper(componentModel = "spring")
public interface VisitMapper {

	VisitDto toDto(Visit visit);

}