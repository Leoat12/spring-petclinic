package org.springframework.samples.petclinic.vet

import java.util.ArrayList
import java.util.HashMap
import java.util.LinkedHashMap
import java.util.Optional

import org.springframework.cache.annotation.Cacheable
import org.springframework.dao.DataAccessException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class JdbcClientVetRepository(private val jdbcClient: JdbcClient) : VetRepository {

	@Cacheable("vets")
	@Transactional(readOnly = true)
	@Throws(DataAccessException::class)
	override fun findAll(): List<Vet> {
		val vets: List<Vet> = jdbcClient.sql("SELECT id, first_name, last_name FROM vets ORDER BY last_name, first_name")
			.query(Vet::class.java)
			.list()
			.mapNotNull { it }
		loadSpecialtiesForVets(vets)
		return vets
	}

	@Cacheable("vets")
	@Transactional(readOnly = true)
	@Throws(DataAccessException::class)
	override fun findAll(pageable: Pageable): Page<Vet> {
		val total = jdbcClient.sql("SELECT COUNT(*) FROM vets").query(Long::class.java).single()
		val vets = if (pageable.isUnpaged) {
			jdbcClient.sql("SELECT id, first_name, last_name FROM vets ORDER BY last_name, first_name")
				.query(Vet::class.java)
				.list()
				.map { it!! }
		}
		else {
			jdbcClient.sql("SELECT id, first_name, last_name FROM vets ORDER BY last_name, first_name LIMIT :limit OFFSET :offset")
				.param("limit", pageable.pageSize)
				.param("offset", pageable.offset)
				.query(Vet::class.java)
				.list()
				.map { it!! }
		}
		loadSpecialtiesForVets(vets)
		return PageImpl(vets, pageable, total)
	}

	@Transactional(readOnly = true)
	override fun findById(id: Int): Optional<Vet> {
		val vet = jdbcClient.sql("SELECT id, first_name, last_name FROM vets WHERE id = :id")
			.param("id", id)
			.query(Vet::class.java)
			.optional()
			.map { it!! }
		vet.ifPresent { v -> loadSpecialtiesForVet(v) }
		return vet
	}

	private fun loadSpecialtiesForVets(vets: List<Vet>) {
		if (vets.isEmpty()) {
			return
		}
		val specialtyMap = LinkedHashMap<Int, Specialty>()
		jdbcClient.sql("SELECT s.id, s.name FROM specialties s ORDER BY s.name")
			.query(Specialty::class.java)
			.list()
			.map { it!! }
			.forEach { s -> specialtyMap[s.id!!] = s }

		val vetSpecialtyMap = HashMap<Int, MutableList<Int>>()
		jdbcClient.sql("SELECT vet_id, specialty_id FROM vet_specialties").query { rs, _ ->
			val vetId = rs.getInt("vet_id")
			val specialtyId = rs.getInt("specialty_id")
			vetSpecialtyMap.computeIfAbsent(vetId) { ArrayList() }.add(specialtyId)
		}.list()

		for (vet in vets) {
			val specialtyIds = vetSpecialtyMap.getOrDefault(vet.id, emptyList())
			for (specialtyId in specialtyIds) {
				val specialty = specialtyMap[specialtyId]
				if (specialty != null) {
					vet.addSpecialty(specialty)
				}
			}
		}
	}

	private fun loadSpecialtiesForVet(vet: Vet) {
		jdbcClient.sql(
				"SELECT s.id, s.name FROM specialties s JOIN vet_specialties vs ON s.id = vs.specialty_id WHERE vs.vet_id = :vetId ORDER BY s.name"
			)
			.param("vetId", vet.id)
			.query(Specialty::class.java)
			.list()
			.map { it!! }
			.forEach { s -> vet.addSpecialty(s) }
	}

}