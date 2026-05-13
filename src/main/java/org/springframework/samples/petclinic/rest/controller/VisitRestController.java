package org.springframework.samples.petclinic.rest.controller;

import java.net.URI;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.Visit;
import org.springframework.samples.petclinic.rest.ResourceNotFoundException;
import org.springframework.samples.petclinic.rest.dto.VisitCreateDto;
import org.springframework.samples.petclinic.rest.dto.VisitDto;
import org.springframework.samples.petclinic.rest.mapper.VisitMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/owners/{ownerId}/pets/{petId}/visits")
class VisitRestController {

	private final OwnerRepository ownerRepository;

	private final VisitMapper visitMapper;

	public VisitRestController(OwnerRepository ownerRepository, VisitMapper visitMapper) {
		this.ownerRepository = ownerRepository;
		this.visitMapper = visitMapper;
	}

	@GetMapping
	java.util.List<VisitDto> list(@PathVariable Integer ownerId, @PathVariable Integer petId) {
		Owner owner = ownerRepository.findById(ownerId)
			.orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + ownerId));
		Pet pet = owner.getPet(petId);
		if (pet == null) {
			throw new ResourceNotFoundException("Pet not found with id: " + petId + " for owner: " + ownerId);
		}
		return pet.getVisits().stream().map(visitMapper::toDto).toList();
	}

	@PostMapping
	ResponseEntity<VisitDto> create(@PathVariable Integer ownerId, @PathVariable Integer petId,
			@Valid @RequestBody VisitCreateDto dto, UriComponentsBuilder uriBuilder) {
		Owner owner = ownerRepository.findById(ownerId)
			.orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + ownerId));
		Pet pet = owner.getPet(petId);
		if (pet == null) {
			throw new ResourceNotFoundException("Pet not found with id: " + petId + " for owner: " + ownerId);
		}
		Visit visit = new Visit();
		if (dto.date() != null) {
			visit.setDate(dto.date());
		}
		visit.setDescription(dto.description());
		pet.addVisit(visit);
		ownerRepository.save(owner);
		URI uri = uriBuilder.path("/api/v1/owners/{ownerId}/pets/{petId}/visits")
			.buildAndExpand(ownerId, petId)
			.toUri();
		return ResponseEntity.created(uri).body(visitMapper.toDto(visit));
	}

}