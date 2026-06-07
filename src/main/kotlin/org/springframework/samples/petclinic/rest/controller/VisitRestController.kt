package org.springframework.samples.petclinic.rest.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.samples.petclinic.owner.OwnerRepository
import org.springframework.samples.petclinic.owner.Pet
import org.springframework.samples.petclinic.owner.Visit
import org.springframework.samples.petclinic.rest.ResourceNotFoundException
import org.springframework.samples.petclinic.rest.dto.VisitCreateDto
import org.springframework.samples.petclinic.rest.dto.VisitDto
import org.springframework.samples.petclinic.rest.mapper.VisitMapper
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder

@RestController
@RequestMapping("/api/v1/owners/{ownerId}/pets/{petId}/visits")
class VisitRestController(
    private val ownerRepository: OwnerRepository,
    private val visitMapper: VisitMapper
) {

    @GetMapping
    fun list(@PathVariable ownerId: Int, @PathVariable petId: Int): List<VisitDto> {
        val owner = ownerRepository.findById(ownerId)
            .orElseThrow { ResourceNotFoundException("Owner not found with id: $ownerId") }
        val pet: Pet? = owner.getPet(petId)
        if (pet == null) {
            throw ResourceNotFoundException("Pet not found with id: $petId for owner: $ownerId")
        }
        return pet.getVisits().map { visitMapper.toDto(it) }
    }

    @PostMapping
    fun create(
        @PathVariable ownerId: Int,
        @PathVariable petId: Int,
        @Valid @RequestBody dto: VisitCreateDto,
        uriBuilder: UriComponentsBuilder
    ): ResponseEntity<VisitDto> {
        val owner = ownerRepository.findById(ownerId)
            .orElseThrow { ResourceNotFoundException("Owner not found with id: $ownerId") }
        val pet: Pet? = owner.getPet(petId)
        if (pet == null) {
            throw ResourceNotFoundException("Pet not found with id: $petId for owner: $ownerId")
        }
        val visit = Visit()
        if (dto.date != null) {
            visit.date = dto.date
        }
        visit.description = dto.description
        pet.addVisit(visit)
        ownerRepository.save(owner)
        val uri = uriBuilder.path("/api/v1/owners/{ownerId}/pets/{petId}/visits")
            .buildAndExpand(ownerId, petId)
            .toUri()
        return ResponseEntity.created(uri).body(visitMapper.toDto(visit))
    }

}