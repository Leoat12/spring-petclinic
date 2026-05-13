package org.springframework.samples.petclinic.rest.controller;

import java.net.URI;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.owner.PetTypeRepository;
import org.springframework.samples.petclinic.rest.ResourceNotFoundException;
import org.springframework.samples.petclinic.rest.dto.PetCreateDto;
import org.springframework.samples.petclinic.rest.dto.PetDto;
import org.springframework.samples.petclinic.rest.mapper.PetMapper;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/owners/{ownerId}/pets")
class PetRestController {

	private final OwnerRepository ownerRepository;

	private final PetTypeRepository petTypeRepository;

	private final PetMapper petMapper;

	public PetRestController(OwnerRepository ownerRepository, PetTypeRepository petTypeRepository,
			PetMapper petMapper) {
		this.ownerRepository = ownerRepository;
		this.petTypeRepository = petTypeRepository;
		this.petMapper = petMapper;
	}

	@GetMapping
	java.util.List<PetDto> list(@PathVariable Integer ownerId) {
		Owner owner = ownerRepository.findById(ownerId)
			.orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + ownerId));
		return owner.getPets().stream().map(petMapper::toDto).toList();
	}

	@GetMapping("/{petId}")
	PetDto detail(@PathVariable Integer ownerId, @PathVariable Integer petId) {
		Owner owner = ownerRepository.findById(ownerId)
			.orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + ownerId));
		Pet pet = owner.getPet(petId);
		if (pet == null) {
			throw new ResourceNotFoundException("Pet not found with id: " + petId + " for owner: " + ownerId);
		}
		return petMapper.toDto(pet);
	}

	@PostMapping
	ResponseEntity<PetDto> create(@PathVariable Integer ownerId, @Valid @RequestBody PetCreateDto dto,
			UriComponentsBuilder uriBuilder) {
		Owner owner = ownerRepository.findById(ownerId)
			.orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + ownerId));
		PetType petType = petTypeRepository.findById(dto.typeId())
			.orElseThrow(() -> new ResourceNotFoundException("PetType not found with id: " + dto.typeId()));
		Pet pet = new Pet();
		pet.setName(dto.name());
		pet.setBirthDate(dto.birthDate());
		pet.setType(petType);
		owner.addPet(pet);
		ownerRepository.save(owner);
		Pet savedPet = owner.getPet(pet.getName());
		URI uri = uriBuilder.path("/api/v1/owners/{ownerId}/pets/{petId}")
			.buildAndExpand(ownerId, savedPet.getId())
			.toUri();
		return ResponseEntity.created(uri).body(petMapper.toDto(savedPet));
	}

	@PutMapping("/{petId}")
	PetDto update(@PathVariable Integer ownerId, @PathVariable Integer petId, @Valid @RequestBody PetCreateDto dto) {
		Owner owner = ownerRepository.findById(ownerId)
			.orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + ownerId));
		Pet pet = owner.getPet(petId);
		if (pet == null) {
			throw new ResourceNotFoundException("Pet not found with id: " + petId + " for owner: " + ownerId);
		}
		PetType petType = petTypeRepository.findById(dto.typeId())
			.orElseThrow(() -> new ResourceNotFoundException("PetType not found with id: " + dto.typeId()));
		pet.setName(dto.name());
		pet.setBirthDate(dto.birthDate());
		pet.setType(petType);
		ownerRepository.save(owner);
		return petMapper.toDto(pet);
	}

	@DeleteMapping("/{petId}")
	ResponseEntity<Void> delete(@PathVariable Integer ownerId, @PathVariable Integer petId) {
		Owner owner = ownerRepository.findById(ownerId)
			.orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + ownerId));
		Pet pet = owner.getPet(petId);
		if (pet == null) {
			throw new ResourceNotFoundException("Pet not found with id: " + petId + " for owner: " + ownerId);
		}
		owner.getPets().remove(pet);
		ownerRepository.save(owner);
		return ResponseEntity.noContent().build();
	}

}