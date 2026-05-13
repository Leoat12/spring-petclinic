package org.springframework.samples.petclinic.rest.dto;

import java.util.List;

public record OwnerDto(Integer id, String firstName, String lastName, String address, String city, String telephone,
		List<PetDto> pets) {
}