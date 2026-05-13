package org.springframework.samples.petclinic.rest.controller;

import java.net.URI;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.rest.ResourceNotFoundException;
import org.springframework.samples.petclinic.rest.dto.OwnerCreateDto;
import org.springframework.samples.petclinic.rest.dto.OwnerDto;
import org.springframework.samples.petclinic.rest.dto.OwnerUpdateDto;
import org.springframework.samples.petclinic.rest.dto.PagedResultDto;
import org.springframework.samples.petclinic.rest.mapper.OwnerMapper;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/owners")
class OwnerRestController {

	private final OwnerRepository ownerRepository;

	private final OwnerMapper ownerMapper;

	public OwnerRestController(OwnerRepository ownerRepository, OwnerMapper ownerMapper) {
		this.ownerRepository = ownerRepository;
		this.ownerMapper = ownerMapper;
	}

	@GetMapping
	PagedResultDto<OwnerDto> list(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "20") int size) {
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("lastName", "firstName"));
		Page<Owner> owners = ownerRepository.findAll(pageable);
		return PagedResultDto.from(owners, ownerMapper::toDto);
	}

	@GetMapping("/{ownerId}")
	OwnerDto detail(@PathVariable Integer ownerId) {
		Owner owner = ownerRepository.findById(ownerId)
			.orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + ownerId));
		return ownerMapper.toDto(owner);
	}

	@PostMapping
	ResponseEntity<OwnerDto> create(@Valid @RequestBody OwnerCreateDto dto, UriComponentsBuilder uriBuilder) {
		Owner owner = ownerMapper.toEntity(dto);
		Owner saved = ownerRepository.save(owner);
		URI uri = uriBuilder.path("/api/v1/owners/{id}").buildAndExpand(saved.getId()).toUri();
		return ResponseEntity.created(uri).body(ownerMapper.toDto(saved));
	}

	@PutMapping("/{ownerId}")
	OwnerDto update(@PathVariable Integer ownerId, @Valid @RequestBody OwnerUpdateDto dto) {
		Owner owner = ownerRepository.findById(ownerId)
			.orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + ownerId));
		ownerMapper.updateEntity(dto, owner);
		Owner saved = ownerRepository.save(owner);
		return ownerMapper.toDto(saved);
	}

	@DeleteMapping("/{ownerId}")
	ResponseEntity<Void> delete(@PathVariable Integer ownerId) {
		if (!ownerRepository.existsById(ownerId)) {
			throw new ResourceNotFoundException("Owner not found with id: " + ownerId);
		}
		ownerRepository.deleteById(ownerId);
		return ResponseEntity.noContent().build();
	}

}