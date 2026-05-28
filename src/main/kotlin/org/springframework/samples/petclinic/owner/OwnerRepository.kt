package org.springframework.samples.petclinic.owner

import java.util.Optional

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface OwnerRepository {

	fun findByLastNameStartingWith(lastName: String, pageable: Pageable): Page<Owner>

	fun findById(id: Int): Optional<Owner>

	fun save(owner: Owner): Owner

	fun findAll(): List<Owner>

	fun findAll(pageable: Pageable): Page<Owner>

	fun existsById(id: Int): Boolean

	fun deleteById(id: Int)

}