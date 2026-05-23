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

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class JdbcClientPetTypeRepository implements PetTypeRepository {

	private final JdbcClient jdbcClient;

	private final JdbcTemplate jdbcTemplate;

	public JdbcClientPetTypeRepository(JdbcClient jdbcClient, JdbcTemplate jdbcTemplate) {
		this.jdbcClient = jdbcClient;
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	@Transactional(readOnly = true)
	public List<PetType> findPetTypes() {
		return jdbcClient.sql("SELECT id, name FROM types ORDER BY name").query(PetType.class).list();
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<PetType> findById(Integer id) {
		return jdbcClient.sql("SELECT id, name FROM types WHERE id = :id")
			.param("id", id)
			.query(PetType.class)
			.optional();
	}

	@Override
	@Transactional
	public PetType save(PetType petType) {
		if (petType.isNew()) {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(con -> {
				var ps = con.prepareStatement("INSERT INTO types (name) VALUES (?)",
						java.sql.Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, petType.getName());
				return ps;
			}, keyHolder);
			petType.setId(keyHolder.getKey().intValue());
		}
		else {
			jdbcTemplate.update("UPDATE types SET name=? WHERE id=?", petType.getName(), petType.getId());
		}
		return petType;
	}

	@Override
	@Transactional
	public void deleteById(Integer id) {
		jdbcClient.sql("DELETE FROM types WHERE id = :id").param("id", id).update();
	}

}