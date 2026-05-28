package org.springframework.samples.petclinic.vet

import java.util.Optional

import org.springframework.cache.annotation.Cacheable
import org.springframework.dao.DataAccessException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface VetRepository {

	@Cacheable("vets")
	@Throws(DataAccessException::class)
	fun findAll(): List<Vet>

	@Cacheable("vets")
	@Throws(DataAccessException::class)
	fun findAll(pageable: Pageable): Page<Vet>

	fun findById(id: Int): Optional<Vet>

}