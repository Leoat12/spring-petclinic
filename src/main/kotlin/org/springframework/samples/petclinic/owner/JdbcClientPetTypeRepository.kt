package org.springframework.samples.petclinic.owner

import java.util.Optional

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class JdbcClientPetTypeRepository(
	private val jdbcClient: JdbcClient,
	private val jdbcTemplate: JdbcTemplate
) : PetTypeRepository {

	@Transactional(readOnly = true)
	override fun findPetTypes(): List<PetType> =
		jdbcClient.sql("SELECT id, name FROM types ORDER BY name").query(PetType::class.java).list().map { it!! }

	@Transactional(readOnly = true)
	override fun findById(id: Int): Optional<PetType> =
		jdbcClient.sql("SELECT id, name FROM types WHERE id = :id")
			.param("id", id)
			.query(PetType::class.java)
			.optional()
			.map { it!! }

	@Transactional
	override fun save(petType: PetType): PetType {
		if (petType.isNew()) {
			val keyHolder = GeneratedKeyHolder()
			jdbcTemplate.update({ con ->
				val ps = con.prepareStatement(
					"INSERT INTO types (name) VALUES (?)",
					java.sql.Statement.RETURN_GENERATED_KEYS
				)
				ps.setString(1, petType.name)
				ps
			}, keyHolder)
			petType.id = keyHolder.getKey()!!.toInt()
		}
		else {
			jdbcTemplate.update("UPDATE types SET name=? WHERE id=?", petType.name, petType.id)
		}
		return petType
	}

	@Transactional
	override fun deleteById(id: Int) {
		jdbcClient.sql("DELETE FROM types WHERE id = :id").param("id", id).update()
	}

}