package org.springframework.samples.petclinic.vet

import java.util.Optional

import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class JdbcClientSpecialtyRepository(private val jdbcClient: JdbcClient) : SpecialtyRepository {

	@Transactional(readOnly = true)
	override fun findAll(): List<Specialty> =
		jdbcClient.sql("SELECT id, name FROM specialties ORDER BY name").query(Specialty::class.java).list().map { it!! }

	@Transactional(readOnly = true)
	override fun findById(id: Int): Optional<Specialty> =
		jdbcClient.sql("SELECT id, name FROM specialties WHERE id = :id")
			.param("id", id)
			.query(Specialty::class.java)
			.optional()
			.map { it!! }

}