package org.springframework.samples.petclinic.rest.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.samples.petclinic.owner.OwnerRepository
import org.springframework.samples.petclinic.owner.Pet
import org.springframework.samples.petclinic.owner.PetType
import org.springframework.samples.petclinic.owner.PetTypeRepository
import org.springframework.samples.petclinic.rest.ResourceNotFoundException
import org.springframework.samples.petclinic.rest.dto.PetCreateDto
import org.springframework.samples.petclinic.rest.dto.PetDto
import org.springframework.samples.petclinic.rest.mapper.PetMapper
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder

@RestController
@RequestMapping("/api/v1/owners/{ownerId}/pets")
class PetRestController(
    private val ownerRepository: OwnerRepository,
    private val petTypeRepository: PetTypeRepository,
    private val petMapper: PetMapper
) {

    @GetMapping
    fun list(@PathVariable ownerId: Int): List<PetDto> {
        val owner = ownerRepository.findById(ownerId)
            .orElseThrow { ResourceNotFoundException("Owner not found with id: $ownerId") }
        return owner.getPets().map { petMapper.toDto(it) }
    }

    @GetMapping("/{petId}")
    fun detail(@PathVariable ownerId: Int, @PathVariable petId: Int): PetDto {
        val owner = ownerRepository.findById(ownerId)
            .orElseThrow { ResourceNotFoundException("Owner not found with id: $ownerId") }
        val pet: Pet? = owner.getPet(petId)
        if (pet == null) {
            throw ResourceNotFoundException("Pet not found with id: $petId for owner: $ownerId")
        }
        return petMapper.toDto(pet)
    }

    @PostMapping
    fun create(
        @PathVariable ownerId: Int,
        @Valid @RequestBody dto: PetCreateDto,
        uriBuilder: UriComponentsBuilder
    ): ResponseEntity<PetDto> {
        val owner = ownerRepository.findById(ownerId)
            .orElseThrow { ResourceNotFoundException("Owner not found with id: $ownerId") }
        val petType: PetType = petTypeRepository.findById(dto.typeId)
            .orElseThrow { ResourceNotFoundException("PetType not found with id: ${dto.typeId}") }
        val pet = Pet()
        pet.name = dto.name
        pet.birthDate = dto.birthDate
        pet.type = petType
        owner.addPet(pet)
        ownerRepository.save(owner)
        val savedPet = owner.getPet(pet.name!!)
        val uri = uriBuilder.path("/api/v1/owners/{ownerId}/pets/{petId}")
            .buildAndExpand(ownerId, savedPet!!.id)
            .toUri()
        return ResponseEntity.created(uri).body(petMapper.toDto(savedPet))
    }

    @PutMapping("/{petId}")
    fun update(
        @PathVariable ownerId: Int,
        @PathVariable petId: Int,
        @Valid @RequestBody dto: PetCreateDto
    ): PetDto {
        val owner = ownerRepository.findById(ownerId)
            .orElseThrow { ResourceNotFoundException("Owner not found with id: $ownerId") }
        val pet: Pet? = owner.getPet(petId)
        if (pet == null) {
            throw ResourceNotFoundException("Pet not found with id: $petId for owner: $ownerId")
        }
        val petType: PetType = petTypeRepository.findById(dto.typeId)
            .orElseThrow { ResourceNotFoundException("PetType not found with id: ${dto.typeId}") }
        pet.name = dto.name
        pet.birthDate = dto.birthDate
        pet.type = petType
        ownerRepository.save(owner)
        return petMapper.toDto(pet)
    }

    @DeleteMapping("/{petId}")
    fun delete(@PathVariable ownerId: Int, @PathVariable petId: Int): ResponseEntity<Void> {
        val owner = ownerRepository.findById(ownerId)
            .orElseThrow { ResourceNotFoundException("Owner not found with id: $ownerId") }
        val pet: Pet? = owner.getPet(petId)
        if (pet == null) {
            throw ResourceNotFoundException("Pet not found with id: $petId for owner: $ownerId")
        }
        owner.getPets().remove(pet)
        ownerRepository.save(owner)
        return ResponseEntity.noContent().build()
    }

}