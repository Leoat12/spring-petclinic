package org.springframework.samples.petclinic.owner

import java.util.ArrayList
import java.util.HashMap
import java.util.Optional

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class JdbcClientOwnerRepository(
	private val jdbcClient: JdbcClient,
	private val jdbcTemplate: JdbcTemplate
) : OwnerRepository {

	private val ownerRowMapper = { rs: java.sql.ResultSet, _: Int ->
		val owner = Owner()
		owner.id = rs.getInt("id")
		owner.firstName = rs.getString("first_name")
		owner.lastName = rs.getString("last_name")
		owner.address = rs.getString("address")
		owner.city = rs.getString("city")
		owner.telephone = rs.getString("telephone")
		owner.email = rs.getString("email")
		owner
	}

	@Transactional(readOnly = true)
	override fun findByLastNameStartingWith(lastName: String, pageable: Pageable): Page<Owner> {
		val total = jdbcClient.sql("SELECT COUNT(*) FROM owners WHERE last_name LIKE :lastName")
			.param("lastName", "$lastName%")
			.query(Long::class.java)
			.single()
		val owners = if (pageable.isUnpaged) {
			jdbcClient.sql(
					"SELECT id, first_name, last_name, address, city, telephone, email FROM owners WHERE last_name LIKE :lastName ORDER BY last_name, first_name"
				)
				.param("lastName", "$lastName%")
				.query(ownerRowMapper)
				.list()
		}
		else {
			jdbcClient.sql(
					"SELECT id, first_name, last_name, address, city, telephone, email FROM owners WHERE last_name LIKE :lastName ORDER BY last_name, first_name LIMIT :limit OFFSET :offset"
				)
				.param("lastName", "$lastName%")
				.param("limit", pageable.pageSize)
				.param("offset", pageable.offset)
				.query(ownerRowMapper)
				.list()
		}
		loadPetsAndVisitsForOwners(owners)
		return PageImpl(owners, pageable, total)
	}

	@Transactional(readOnly = true)
	override fun findById(id: Int): Optional<Owner> {
		val owner = jdbcClient
			.sql("SELECT id, first_name, last_name, address, city, telephone, email FROM owners WHERE id = :id")
			.param("id", id)
			.query(ownerRowMapper)
			.optional()
		owner.ifPresent { o -> loadPetsAndVisitsForOwner(o) }
		return owner
	}

	@Transactional
	override fun save(owner: Owner): Owner {
		if (owner.isNew()) {
			val keyHolder = GeneratedKeyHolder()
			jdbcTemplate.update({ con ->
				val ps = con.prepareStatement(
					"INSERT INTO owners (first_name, last_name, address, city, telephone, email) VALUES (?, ?, ?, ?, ?, ?)",
					java.sql.Statement.RETURN_GENERATED_KEYS
				)
				ps.setString(1, owner.firstName)
				ps.setString(2, owner.lastName)
				ps.setString(3, owner.address)
				ps.setString(4, owner.city)
				ps.setString(5, owner.telephone)
				ps.setString(6, owner.email)
				ps
			}, keyHolder)
			owner.id = keyHolder.getKey()!!.toInt()
		}
		else {
			jdbcTemplate.update(
				"UPDATE owners SET first_name=?, last_name=?, address=?, city=?, telephone=?, email=? WHERE id=?",
				owner.firstName, owner.lastName, owner.address, owner.city,
				owner.telephone, owner.email, owner.id
			)
		}
		savePets(owner)
		return owner
	}

	@Transactional(readOnly = true)
	override fun findAll(): List<Owner> {
		val owners = jdbcClient.sql(
				"SELECT id, first_name, last_name, address, city, telephone, email FROM owners ORDER BY last_name, first_name"
			)
			.query(ownerRowMapper)
			.list()
		loadPetsAndVisitsForOwners(owners)
		return owners
	}

	@Transactional(readOnly = true)
	override fun findAll(pageable: Pageable): Page<Owner> {
		val total = jdbcClient.sql("SELECT COUNT(*) FROM owners").query(Long::class.java).single()
		val owners = if (pageable.isUnpaged) {
			jdbcClient.sql(
					"SELECT id, first_name, last_name, address, city, telephone, email FROM owners ORDER BY last_name, first_name"
				)
				.query(ownerRowMapper)
				.list()
		}
		else {
			jdbcClient.sql(
					"SELECT id, first_name, last_name, address, city, telephone, email FROM owners ORDER BY last_name, first_name LIMIT :limit OFFSET :offset"
				)
				.param("limit", pageable.pageSize)
				.param("offset", pageable.offset)
				.query(ownerRowMapper)
				.list()
		}
		loadPetsAndVisitsForOwners(owners)
		return PageImpl(owners, pageable, total)
	}

	@Transactional(readOnly = true)
	override fun existsById(id: Int): Boolean {
		val count = jdbcClient.sql("SELECT COUNT(*) FROM owners WHERE id = :id")
			.param("id", id)
			.query(Int::class.java)
			.single()
		return count > 0
	}

	@Transactional
	override fun deleteById(id: Int) {
		deleteVisitsByOwnerId(id)
		deletePetsByOwnerId(id)
		jdbcClient.sql("DELETE FROM owners WHERE id = :id").param("id", id).update()
	}

	private fun loadPetsAndVisitsForOwners(owners: List<Owner>) {
		if (owners.isEmpty()) {
			return
		}
		val typeMap = loadAllPetTypes()
		val ownerIds = owners.map { it.id!! }
		val petsByOwner = HashMap<Int, MutableList<Pet>>()
		jdbcClient
			.sql("SELECT id, name, birth_date, type_id, owner_id FROM pets WHERE owner_id IN (:ownerIds) ORDER BY name")
			.param("ownerIds", ownerIds)
			.query { rs, _ ->
				val pet = Pet()
				pet.id = rs.getInt("id")
				pet.name = rs.getString("name")
				pet.birthDate = rs.getDate("birth_date").toLocalDate()
				pet.typeId = rs.getInt("type_id")
				pet.type = typeMap[pet.typeId]
				val ownerId = rs.getInt("owner_id")
				petsByOwner.computeIfAbsent(ownerId) { ArrayList() }.add(pet)
				pet
			}.list()
		val visitsByPet = loadVisitsByOwnerIds(ownerIds)
		for (owner in owners) {
			val pets = petsByOwner.getOrDefault(owner.id, emptyList())
			for (pet in pets) {
				pet.getVisits().addAll(visitsByPet.getOrDefault(pet.id, emptyList()))
				owner.getPets().add(pet)
			}
		}
	}

	private fun loadPetsAndVisitsForOwner(owner: Owner) {
		val typeMap = loadAllPetTypes()
		val pets = jdbcClient
			.sql("SELECT id, name, birth_date, type_id, owner_id FROM pets WHERE owner_id = :ownerId ORDER BY name")
			.param("ownerId", owner.id)
			.query { rs, _ ->
				val pet = Pet()
				pet.id = rs.getInt("id")
				pet.name = rs.getString("name")
				pet.birthDate = rs.getDate("birth_date").toLocalDate()
				pet.typeId = rs.getInt("type_id")
				pet.type = typeMap[pet.typeId]
				pet
			}.list()
		val visitsByPet = loadVisitsByOwnerId(owner.id!!)
		for (pet in pets) {
			pet.getVisits().addAll(visitsByPet.getOrDefault(pet.id, emptyList()))
			owner.getPets().add(pet)
		}
	}

	private fun loadAllPetTypes(): Map<Int, PetType> {
		val typeMap = HashMap<Int, PetType>()
		jdbcClient.sql("SELECT id, name FROM types ORDER BY name").query { rs, _ ->
			val type = PetType()
			type.id = rs.getInt("id")
			type.name = rs.getString("name")
			typeMap[type.id!!] = type
			type
		}.list()
		return typeMap
	}

	private fun loadVisitsByOwnerIds(ownerIds: List<Int>): Map<Int, MutableList<Visit>> {
		val visitsByPet = HashMap<Int, MutableList<Visit>>()
		jdbcClient.sql(
				"SELECT v.id, v.pet_id, v.visit_date, v.description FROM visits v JOIN pets p ON v.pet_id = p.id WHERE p.owner_id IN (:ownerIds) ORDER BY v.visit_date"
			)
			.param("ownerIds", ownerIds)
			.query { rs, _ ->
				val visit = Visit()
				visit.id = rs.getInt("id")
				visit.date = rs.getDate("visit_date").toLocalDate()
				visit.description = rs.getString("description")
				val petId = rs.getInt("pet_id")
				visitsByPet.computeIfAbsent(petId) { ArrayList() }.add(visit)
				visit
			}.list()
		return visitsByPet
	}

	private fun loadVisitsByOwnerId(ownerId: Int): Map<Int, MutableList<Visit>> {
		val visitsByPet = HashMap<Int, MutableList<Visit>>()
		jdbcClient.sql(
				"SELECT v.id, v.pet_id, v.visit_date, v.description FROM visits v JOIN pets p ON v.pet_id = p.id WHERE p.owner_id = :ownerId ORDER BY v.visit_date"
			)
			.param("ownerId", ownerId)
			.query { rs, _ ->
				val visit = Visit()
				visit.id = rs.getInt("id")
				visit.date = rs.getDate("visit_date").toLocalDate()
				visit.description = rs.getString("description")
				val petId = rs.getInt("pet_id")
				visitsByPet.computeIfAbsent(petId) { ArrayList() }.add(visit)
				visit
			}.list()
		return visitsByPet
	}

	private fun savePets(owner: Owner) {
		deleteRemovedPets(owner)
		for (pet in owner.getPets()) {
			if (pet.isNew()) {
				pet.typeId = if (pet.type != null) pet.type!!.id else pet.typeId
				val keyHolder = GeneratedKeyHolder()
				jdbcTemplate.update({ con ->
					val ps = con.prepareStatement(
						"INSERT INTO pets (name, birth_date, type_id, owner_id) VALUES (?, ?, ?, ?)",
						java.sql.Statement.RETURN_GENERATED_KEYS
					)
					ps.setString(1, pet.name)
					ps.setDate(2, java.sql.Date.valueOf(pet.birthDate))
					ps.setInt(3, pet.typeId!!)
					ps.setInt(4, owner.id!!)
					ps
				}, keyHolder)
				pet.id = keyHolder.getKey()!!.toInt()
			}
			else {
				pet.typeId = if (pet.type != null) pet.type!!.id else pet.typeId
				jdbcTemplate.update(
					"UPDATE pets SET name=?, birth_date=?, type_id=?, owner_id=? WHERE id=?",
					pet.name, java.sql.Date.valueOf(pet.birthDate), pet.typeId, owner.id, pet.id
				)
			}
			saveVisits(pet)
		}
	}

	private fun deleteRemovedPets(owner: Owner) {
		val currentPetIds = owner.getPets().filter { !it.isNew() }.map { it.id!! }
		if (currentPetIds.isEmpty()) {
			jdbcClient.sql("DELETE FROM visits WHERE pet_id IN (SELECT id FROM pets WHERE owner_id = :ownerId)")
				.param("ownerId", owner.id)
				.update()
			jdbcClient.sql("DELETE FROM pets WHERE owner_id = :ownerId").param("ownerId", owner.id).update()
		}
		else {
			jdbcClient.sql(
					"DELETE FROM visits WHERE pet_id IN (SELECT id FROM pets WHERE owner_id = :ownerId AND id NOT IN (:petIds))"
				)
				.param("ownerId", owner.id)
				.param("petIds", currentPetIds)
				.update()
			jdbcClient.sql("DELETE FROM pets WHERE owner_id = :ownerId AND id NOT IN (:petIds)")
				.param("ownerId", owner.id)
				.param("petIds", currentPetIds)
				.update()
		}
	}

	private fun saveVisits(pet: Pet) {
		for (visit in pet.getVisits()) {
			if (visit.isNew()) {
				val keyHolder = GeneratedKeyHolder()
				jdbcTemplate.update({ con ->
					val ps = con.prepareStatement(
						"INSERT INTO visits (pet_id, visit_date, description) VALUES (?, ?, ?)",
						java.sql.Statement.RETURN_GENERATED_KEYS
					)
					ps.setInt(1, pet.id!!)
					ps.setDate(2, java.sql.Date.valueOf(visit.date))
					ps.setString(3, visit.description)
					ps
				}, keyHolder)
				visit.id = keyHolder.getKey()!!.toInt()
			}
			else {
				jdbcTemplate.update(
					"UPDATE visits SET pet_id=?, visit_date=?, description=? WHERE id=?",
					pet.id, java.sql.Date.valueOf(visit.date), visit.description, visit.id
				)
			}
		}
	}

	private fun deleteVisitsByOwnerId(ownerId: Int) {
		jdbcClient.sql("DELETE FROM visits WHERE pet_id IN (SELECT id FROM pets WHERE owner_id = :ownerId)")
			.param("ownerId", ownerId)
			.update()
	}

	private fun deletePetsByOwnerId(ownerId: Int) {
		jdbcClient.sql("DELETE FROM pets WHERE owner_id = :ownerId").param("ownerId", ownerId).update()
	}

}