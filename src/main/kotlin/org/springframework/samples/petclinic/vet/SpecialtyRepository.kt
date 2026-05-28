package org.springframework.samples.petclinic.vet

import java.util.Optional

interface SpecialtyRepository {

	fun findAll(): List<Specialty>

	fun findById(id: Int): Optional<Specialty>

}