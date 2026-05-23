/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class JdbcClientOwnerRepository implements OwnerRepository {

	private final JdbcClient jdbcClient;

	private final JdbcTemplate jdbcTemplate;

	private final RowMapper<Owner> ownerRowMapper = (rs, rowNum) -> {
		Owner owner = new Owner();
		owner.setId(rs.getInt("id"));
		owner.setFirstName(rs.getString("first_name"));
		owner.setLastName(rs.getString("last_name"));
		owner.setAddress(rs.getString("address"));
		owner.setCity(rs.getString("city"));
		owner.setTelephone(rs.getString("telephone"));
		return owner;
	};

	public JdbcClientOwnerRepository(JdbcClient jdbcClient, JdbcTemplate jdbcTemplate) {
		this.jdbcClient = jdbcClient;
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Owner> findByLastNameStartingWith(String lastName, Pageable pageable) {
		Long total = jdbcClient.sql("SELECT COUNT(*) FROM owners WHERE last_name LIKE :lastName")
			.param("lastName", lastName + "%")
			.query(Long.class)
			.single();
		List<Owner> owners;
		if (pageable.isUnpaged()) {
			owners = jdbcClient.sql(
					"SELECT id, first_name, last_name, address, city, telephone FROM owners WHERE last_name LIKE :lastName ORDER BY last_name, first_name")
				.param("lastName", lastName + "%")
				.query(ownerRowMapper)
				.list();
		}
		else {
			owners = jdbcClient.sql(
					"SELECT id, first_name, last_name, address, city, telephone FROM owners WHERE last_name LIKE :lastName ORDER BY last_name, first_name LIMIT :limit OFFSET :offset")
				.param("lastName", lastName + "%")
				.param("limit", pageable.getPageSize())
				.param("offset", pageable.getOffset())
				.query(ownerRowMapper)
				.list();
		}
		loadPetsAndVisitsForOwners(owners);
		return new PageImpl<>(owners, pageable, total);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Owner> findById(Integer id) {
		Optional<Owner> owner = jdbcClient
			.sql("SELECT id, first_name, last_name, address, city, telephone FROM owners WHERE id = :id")
			.param("id", id)
			.query(ownerRowMapper)
			.optional();
		owner.ifPresent(o -> loadPetsAndVisitsForOwner(o));
		return owner;
	}

	@Override
	@Transactional
	public Owner save(Owner owner) {
		if (owner.isNew()) {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(con -> {
				var ps = con.prepareStatement(
						"INSERT INTO owners (first_name, last_name, address, city, telephone) VALUES (?, ?, ?, ?, ?)",
						java.sql.Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, owner.getFirstName());
				ps.setString(2, owner.getLastName());
				ps.setString(3, owner.getAddress());
				ps.setString(4, owner.getCity());
				ps.setString(5, owner.getTelephone());
				return ps;
			}, keyHolder);
			owner.setId(keyHolder.getKey().intValue());
		}
		else {
			jdbcTemplate.update(
					"UPDATE owners SET first_name=?, last_name=?, address=?, city=?, telephone=? WHERE id=?",
					owner.getFirstName(), owner.getLastName(), owner.getAddress(), owner.getCity(),
					owner.getTelephone(), owner.getId());
		}
		savePets(owner);
		return owner;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Owner> findAll() {
		List<Owner> owners = jdbcClient.sql(
				"SELECT id, first_name, last_name, address, city, telephone FROM owners ORDER BY last_name, first_name")
			.query(ownerRowMapper)
			.list();
		loadPetsAndVisitsForOwners(owners);
		return owners;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Owner> findAll(Pageable pageable) {
		Long total = jdbcClient.sql("SELECT COUNT(*) FROM owners").query(Long.class).single();
		List<Owner> owners;
		if (pageable.isUnpaged()) {
			owners = jdbcClient.sql(
					"SELECT id, first_name, last_name, address, city, telephone FROM owners ORDER BY last_name, first_name")
				.query(ownerRowMapper)
				.list();
		}
		else {
			owners = jdbcClient.sql(
					"SELECT id, first_name, last_name, address, city, telephone FROM owners ORDER BY last_name, first_name LIMIT :limit OFFSET :offset")
				.param("limit", pageable.getPageSize())
				.param("offset", pageable.getOffset())
				.query(ownerRowMapper)
				.list();
		}
		loadPetsAndVisitsForOwners(owners);
		return new PageImpl<>(owners, pageable, total);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsById(Integer id) {
		Integer count = jdbcClient.sql("SELECT COUNT(*) FROM owners WHERE id = :id")
			.param("id", id)
			.query(Integer.class)
			.single();
		return count > 0;
	}

	@Override
	@Transactional
	public void deleteById(Integer id) {
		deleteVisitsByOwnerId(id);
		deletePetsByOwnerId(id);
		jdbcClient.sql("DELETE FROM owners WHERE id = :id").param("id", id).update();
	}

	private void loadPetsAndVisitsForOwners(List<Owner> owners) {
		if (owners.isEmpty()) {
			return;
		}
		Map<Integer, PetType> typeMap = loadAllPetTypes();
		List<Integer> ownerIds = owners.stream().map(Owner::getId).toList();
		Map<Integer, List<Pet>> petsByOwner = new HashMap<>();
		jdbcClient
			.sql("SELECT id, name, birth_date, type_id, owner_id FROM pets WHERE owner_id IN (:ownerIds) ORDER BY name")
			.param("ownerIds", ownerIds)
			.query((rs, rowNum) -> {
				Pet pet = new Pet();
				pet.setId(rs.getInt("id"));
				pet.setName(rs.getString("name"));
				pet.setBirthDate(rs.getDate("birth_date").toLocalDate());
				pet.setTypeId(rs.getInt("type_id"));
				pet.setType(typeMap.get(pet.getTypeId()));
				int ownerId = rs.getInt("owner_id");
				petsByOwner.computeIfAbsent(ownerId, k -> new ArrayList<>()).add(pet);
				return pet;
			})
			.list();
		Map<Integer, List<Visit>> visitsByPet = loadVisitsByOwnerIds(ownerIds);
		for (Owner owner : owners) {
			List<Pet> pets = petsByOwner.getOrDefault(owner.getId(), List.of());
			for (Pet pet : pets) {
				pet.getVisits().addAll(visitsByPet.getOrDefault(pet.getId(), List.of()));
				owner.getPets().add(pet);
			}
		}
	}

	private void loadPetsAndVisitsForOwner(Owner owner) {
		Map<Integer, PetType> typeMap = loadAllPetTypes();
		List<Pet> pets = jdbcClient
			.sql("SELECT id, name, birth_date, type_id, owner_id FROM pets WHERE owner_id = :ownerId ORDER BY name")
			.param("ownerId", owner.getId())
			.query((rs, rowNum) -> {
				Pet pet = new Pet();
				pet.setId(rs.getInt("id"));
				pet.setName(rs.getString("name"));
				pet.setBirthDate(rs.getDate("birth_date").toLocalDate());
				pet.setTypeId(rs.getInt("type_id"));
				pet.setType(typeMap.get(pet.getTypeId()));
				return pet;
			})
			.list();
		Map<Integer, List<Visit>> visitsByPet = loadVisitsByOwnerId(owner.getId());
		for (Pet pet : pets) {
			pet.getVisits().addAll(visitsByPet.getOrDefault(pet.getId(), List.of()));
			owner.getPets().add(pet);
		}
	}

	private Map<Integer, PetType> loadAllPetTypes() {
		Map<Integer, PetType> typeMap = new HashMap<>();
		jdbcClient.sql("SELECT id, name FROM types ORDER BY name").query((rs, rowNum) -> {
			PetType type = new PetType();
			type.setId(rs.getInt("id"));
			type.setName(rs.getString("name"));
			typeMap.put(type.getId(), type);
			return type;
		}).list();
		return typeMap;
	}

	private Map<Integer, List<Visit>> loadVisitsByOwnerIds(List<Integer> ownerIds) {
		Map<Integer, List<Visit>> visitsByPet = new HashMap<>();
		jdbcClient.sql(
				"SELECT v.id, v.pet_id, v.visit_date, v.description FROM visits v JOIN pets p ON v.pet_id = p.id WHERE p.owner_id IN (:ownerIds) ORDER BY v.visit_date")
			.param("ownerIds", ownerIds)
			.query((rs, rowNum) -> {
				Visit visit = new Visit();
				visit.setId(rs.getInt("id"));
				visit.setDate(rs.getDate("visit_date").toLocalDate());
				visit.setDescription(rs.getString("description"));
				int petId = rs.getInt("pet_id");
				visitsByPet.computeIfAbsent(petId, k -> new ArrayList<>()).add(visit);
				return visit;
			})
			.list();
		return visitsByPet;
	}

	private Map<Integer, List<Visit>> loadVisitsByOwnerId(Integer ownerId) {
		Map<Integer, List<Visit>> visitsByPet = new HashMap<>();
		jdbcClient.sql(
				"SELECT v.id, v.pet_id, v.visit_date, v.description FROM visits v JOIN pets p ON v.pet_id = p.id WHERE p.owner_id = :ownerId ORDER BY v.visit_date")
			.param("ownerId", ownerId)
			.query((rs, rowNum) -> {
				Visit visit = new Visit();
				visit.setId(rs.getInt("id"));
				visit.setDate(rs.getDate("visit_date").toLocalDate());
				visit.setDescription(rs.getString("description"));
				int petId = rs.getInt("pet_id");
				visitsByPet.computeIfAbsent(petId, k -> new ArrayList<>()).add(visit);
				return visit;
			})
			.list();
		return visitsByPet;
	}

	private void savePets(Owner owner) {
		deleteRemovedPets(owner);
		for (Pet pet : owner.getPets()) {
			if (pet.isNew()) {
				pet.setTypeId(pet.getType() != null ? pet.getType().getId() : pet.getTypeId());
				KeyHolder keyHolder = new GeneratedKeyHolder();
				jdbcTemplate.update(con -> {
					var ps = con.prepareStatement(
							"INSERT INTO pets (name, birth_date, type_id, owner_id) VALUES (?, ?, ?, ?)",
							java.sql.Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, pet.getName());
					ps.setDate(2, java.sql.Date.valueOf(pet.getBirthDate()));
					ps.setInt(3, pet.getTypeId());
					ps.setInt(4, owner.getId());
					return ps;
				}, keyHolder);
				pet.setId(keyHolder.getKey().intValue());
			}
			else {
				pet.setTypeId(pet.getType() != null ? pet.getType().getId() : pet.getTypeId());
				jdbcTemplate.update("UPDATE pets SET name=?, birth_date=?, type_id=?, owner_id=? WHERE id=?",
						pet.getName(), java.sql.Date.valueOf(pet.getBirthDate()), pet.getTypeId(), owner.getId(),
						pet.getId());
			}
			saveVisits(pet);
		}
	}

	private void deleteRemovedPets(Owner owner) {
		List<Integer> currentPetIds = owner.getPets().stream().filter(p -> !p.isNew()).map(Pet::getId).toList();
		if (currentPetIds.isEmpty()) {
			jdbcClient.sql("DELETE FROM visits WHERE pet_id IN (SELECT id FROM pets WHERE owner_id = :ownerId)")
				.param("ownerId", owner.getId())
				.update();
			jdbcClient.sql("DELETE FROM pets WHERE owner_id = :ownerId").param("ownerId", owner.getId()).update();
		}
		else {
			jdbcClient.sql(
					"DELETE FROM visits WHERE pet_id IN (SELECT id FROM pets WHERE owner_id = :ownerId AND id NOT IN (:petIds))")
				.param("ownerId", owner.getId())
				.param("petIds", currentPetIds)
				.update();
			jdbcClient.sql("DELETE FROM pets WHERE owner_id = :ownerId AND id NOT IN (:petIds)")
				.param("ownerId", owner.getId())
				.param("petIds", currentPetIds)
				.update();
		}
	}

	private void saveVisits(Pet pet) {
		for (Visit visit : pet.getVisits()) {
			if (visit.isNew()) {
				KeyHolder keyHolder = new GeneratedKeyHolder();
				jdbcTemplate.update(con -> {
					var ps = con.prepareStatement(
							"INSERT INTO visits (pet_id, visit_date, description) VALUES (?, ?, ?)",
							java.sql.Statement.RETURN_GENERATED_KEYS);
					ps.setInt(1, pet.getId());
					ps.setDate(2, java.sql.Date.valueOf(visit.getDate()));
					ps.setString(3, visit.getDescription());
					return ps;
				}, keyHolder);
				visit.setId(keyHolder.getKey().intValue());
			}
			else {
				jdbcTemplate.update("UPDATE visits SET pet_id=?, visit_date=?, description=? WHERE id=?", pet.getId(),
						java.sql.Date.valueOf(visit.getDate()), visit.getDescription(), visit.getId());
			}
		}
	}

	private void deleteVisitsByOwnerId(Integer ownerId) {
		jdbcClient.sql("DELETE FROM visits WHERE pet_id IN (SELECT id FROM pets WHERE owner_id = :ownerId)")
			.param("ownerId", ownerId)
			.update();
	}

	private void deletePetsByOwnerId(Integer ownerId) {
		jdbcClient.sql("DELETE FROM pets WHERE owner_id = :ownerId").param("ownerId", ownerId).update();
	}

}