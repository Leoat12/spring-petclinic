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
package org.springframework.samples.petclinic.vet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * JDBC-based implementation of {@link VetRepository} using {@link JdbcClient}.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 */
@Repository
public class JdbcClientVetRepository implements VetRepository {

	private final JdbcClient jdbcClient;

	public JdbcClientVetRepository(JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	@Override
	@Cacheable("vets")
	@Transactional(readOnly = true)
	public Collection<Vet> findAll() throws DataAccessException {
		List<Vet> vets = jdbcClient.sql("SELECT id, first_name, last_name FROM vets ORDER BY last_name, first_name")
			.query(Vet.class)
			.list();
		loadSpecialtiesForVets(vets);
		return vets;
	}

	@Override
	@Cacheable("vets")
	@Transactional(readOnly = true)
	public Page<Vet> findAll(Pageable pageable) throws DataAccessException {
		Long total = jdbcClient.sql("SELECT COUNT(*) FROM vets").query(Long.class).single();
		List<Vet> vets;
		if (pageable.isUnpaged()) {
			vets = jdbcClient.sql("SELECT id, first_name, last_name FROM vets ORDER BY last_name, first_name")
				.query(Vet.class)
				.list();
		}
		else {
			vets = jdbcClient.sql(
					"SELECT id, first_name, last_name FROM vets ORDER BY last_name, first_name LIMIT :limit OFFSET :offset")
				.param("limit", pageable.getPageSize())
				.param("offset", pageable.getOffset())
				.query(Vet.class)
				.list();
		}
		loadSpecialtiesForVets(vets);
		return new PageImpl<>(vets, pageable, total);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Vet> findById(Integer id) {
		Optional<Vet> vet = jdbcClient.sql("SELECT id, first_name, last_name FROM vets WHERE id = :id")
			.param("id", id)
			.query(Vet.class)
			.optional();
		vet.ifPresent(v -> loadSpecialtiesForVet(v));
		return vet;
	}

	private void loadSpecialtiesForVets(List<Vet> vets) {
		if (vets.isEmpty()) {
			return;
		}
		Map<Integer, Specialty> specialtyMap = new LinkedHashMap<>();
		jdbcClient.sql("SELECT s.id, s.name FROM specialties s ORDER BY s.name")
			.query(Specialty.class)
			.list()
			.forEach(s -> specialtyMap.put(s.getId(), s));

		Map<Integer, List<Integer>> vetSpecialtyMap = new HashMap<>();
		jdbcClient.sql("SELECT vet_id, specialty_id FROM vet_specialties").query((rs, rowNum) -> {
			int vetId = rs.getInt("vet_id");
			int specialtyId = rs.getInt("specialty_id");
			vetSpecialtyMap.computeIfAbsent(vetId, k -> new ArrayList<>()).add(specialtyId);
			return null;
		}).list();

		for (Vet vet : vets) {
			List<Integer> specialtyIds = vetSpecialtyMap.getOrDefault(vet.getId(), List.of());
			for (Integer specialtyId : specialtyIds) {
				Specialty specialty = specialtyMap.get(specialtyId);
				if (specialty != null) {
					vet.addSpecialty(specialty);
				}
			}
		}
	}

	private void loadSpecialtiesForVet(Vet vet) {
		Map<Integer, Specialty> specialtyMap = new LinkedHashMap<>();
		jdbcClient.sql(
				"SELECT s.id, s.name FROM specialties s JOIN vet_specialties vs ON s.id = vs.specialty_id WHERE vs.vet_id = :vetId ORDER BY s.name")
			.param("vetId", vet.getId())
			.query(Specialty.class)
			.list()
			.forEach(s -> {
				specialtyMap.put(s.getId(), s);
				vet.addSpecialty(s);
			});
	}

}