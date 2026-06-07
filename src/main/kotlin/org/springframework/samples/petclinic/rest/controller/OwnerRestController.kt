package org.springframework.samples.petclinic.rest.controller

import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.samples.petclinic.owner.Owner
import org.springframework.samples.petclinic.owner.OwnerRepository
import org.springframework.samples.petclinic.rest.ResourceNotFoundException
import org.springframework.samples.petclinic.rest.dto.OwnerCreateDto
import org.springframework.samples.petclinic.rest.dto.OwnerDto
import org.springframework.samples.petclinic.rest.dto.OwnerUpdateDto
import org.springframework.samples.petclinic.rest.dto.PagedResultDto
import org.springframework.samples.petclinic.rest.mapper.OwnerMapper
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder

@RestController
@RequestMapping("/api/v1/owners")
class OwnerRestController(
    private val ownerRepository: OwnerRepository,
    private val ownerMapper: OwnerMapper
) {

    @GetMapping
    fun list(@RequestParam(defaultValue = "1") page: Int, @RequestParam(defaultValue = "20") size: Int): PagedResultDto<OwnerDto> {
        val pageable: Pageable = PageRequest.of(page - 1, size, Sort.by("lastName", "firstName"))
        val owners: Page<Owner> = ownerRepository.findAll(pageable)
        return PagedResultDto.from(owners, ownerMapper::toDto)
    }

    @GetMapping("/{ownerId}")
    fun detail(@PathVariable ownerId: Int): OwnerDto {
        val owner = ownerRepository.findById(ownerId)
            .orElseThrow { ResourceNotFoundException("Owner not found with id: $ownerId") }
        return ownerMapper.toDto(owner)
    }

    @PostMapping
    fun create(@Valid @RequestBody dto: OwnerCreateDto, uriBuilder: UriComponentsBuilder): ResponseEntity<OwnerDto> {
        val owner = ownerMapper.toEntity(dto)
        val saved = ownerRepository.save(owner)
        val uri = uriBuilder.path("/api/v1/owners/{id}").buildAndExpand(saved.id).toUri()
        return ResponseEntity.created(uri).body(ownerMapper.toDto(saved))
    }

    @PutMapping("/{ownerId}")
    fun update(@PathVariable ownerId: Int, @Valid @RequestBody dto: OwnerUpdateDto): OwnerDto {
        val owner = ownerRepository.findById(ownerId)
            .orElseThrow { ResourceNotFoundException("Owner not found with id: $ownerId") }
        ownerMapper.updateEntity(dto, owner)
        val saved = ownerRepository.save(owner)
        return ownerMapper.toDto(saved)
    }

    @DeleteMapping("/{ownerId}")
    fun delete(@PathVariable ownerId: Int): ResponseEntity<Void> {
        if (!ownerRepository.existsById(ownerId)) {
            throw ResourceNotFoundException("Owner not found with id: $ownerId")
        }
        ownerRepository.deleteById(ownerId)
        return ResponseEntity.noContent().build()
    }

}