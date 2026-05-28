package org.springframework.samples.petclinic.owner

import java.util.Optional

interface PetTypeRepository {

	fun findPetTypes(): List<PetType>

	fun findById(id: Int): Optional<PetType>

	fun save(petType: PetType): PetType

	fun deleteById(id: Int)

}