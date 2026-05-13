package org.springframework.samples.petclinic.rest.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.samples.petclinic.rest.ResourceNotFoundException;
import org.springframework.samples.petclinic.rest.dto.PagedResultDto;
import org.springframework.samples.petclinic.rest.dto.VetDto;
import org.springframework.samples.petclinic.rest.mapper.VetMapper;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/vets")
class VetRestController {

	private final VetRepository vetRepository;

	private final VetMapper vetMapper;

	public VetRestController(VetRepository vetRepository, VetMapper vetMapper) {
		this.vetRepository = vetRepository;
		this.vetMapper = vetMapper;
	}

	@GetMapping
	PagedResultDto<VetDto> list(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "20") int size) {
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("lastName", "firstName"));
		Page<Vet> vets = vetRepository.findAll(pageable);
		return PagedResultDto.from(vets, vetMapper::toDto);
	}

	@GetMapping("/{vetId}")
	VetDto detail(@PathVariable Integer vetId) {
		Vet vet = vetRepository.findById(vetId)
			.orElseThrow(() -> new ResourceNotFoundException("Vet not found with id: " + vetId));
		return vetMapper.toDto(vet);
	}

}