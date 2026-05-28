package org.springframework.samples.petclinic.rest.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.rest.dto.OwnerDto;
import org.springframework.samples.petclinic.rest.mapper.OwnerMapper;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OwnerRestController.class)
class OwnerRestControllerTests {

	private static final int TEST_OWNER_ID = 1;

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OwnerRepository ownerRepository;

	@MockitoBean
	private OwnerMapper ownerMapper;

	private Owner george() {
		Owner george = new Owner();
		george.setId(TEST_OWNER_ID);
		george.setFirstName("George");
		george.setLastName("Franklin");
		george.setAddress("110 W. Liberty St.");
		george.setCity("Madison");
		george.setTelephone("6085551023");
		george.setEmail("george@franklin.com");
		return george;
	}

	@BeforeEach
	void setup() {
		Owner george = george();
		given(ownerRepository.findAll(any(Pageable.class))).willReturn(new PageImpl<>(List.of(george)));
		given(ownerRepository.findById(TEST_OWNER_ID)).willReturn(Optional.of(george));
		given(ownerRepository.existsById(TEST_OWNER_ID)).willReturn(true);
		given(ownerRepository.save(any(Owner.class))).willAnswer(invocation -> {
			Owner owner = invocation.getArgument(0);
			if (owner.getId() == null) {
				owner.setId(2);
			}
			return owner;
		});
		given(ownerMapper.toDto(any(Owner.class))).willAnswer(invocation -> {
			Owner owner = invocation.getArgument(0);
			return new OwnerDto(owner.getId(), owner.getFirstName(), owner.getLastName(), owner.getAddress(),
					owner.getCity(), owner.getTelephone(), owner.getEmail(), List.of());
		});
		given(ownerMapper.toEntity(any(org.springframework.samples.petclinic.rest.dto.OwnerCreateDto.class)))
			.willAnswer(invocation -> {
				org.springframework.samples.petclinic.rest.dto.OwnerCreateDto dto = invocation.getArgument(0);
				Owner owner = new Owner();
				owner.setFirstName(dto.firstName());
				owner.setLastName(dto.lastName());
				owner.setAddress(dto.address());
				owner.setCity(dto.city());
				owner.setTelephone(dto.telephone());
				owner.setEmail(dto.email());
				return owner;
			});
		doAnswer(invocation -> {
			org.springframework.samples.petclinic.rest.dto.OwnerUpdateDto dto = invocation.getArgument(0);
			Owner owner = invocation.getArgument(1);
			owner.setFirstName(dto.firstName());
			owner.setLastName(dto.lastName());
			owner.setAddress(dto.address());
			owner.setCity(dto.city());
			owner.setTelephone(dto.telephone());
			owner.setEmail(dto.email());
			return null;
		}).when(ownerMapper)
			.updateEntity(any(org.springframework.samples.petclinic.rest.dto.OwnerUpdateDto.class), any(Owner.class));
	}

	@Test
	void listOwners() throws Exception {
		mockMvc.perform(get("/api/v1/owners").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.content[0].firstName").value("George"))
			.andExpect(jsonPath("$.totalElements").value(1));
	}

	@Test
	void getOwner() throws Exception {
		mockMvc.perform(get("/api/v1/owners/{ownerId}", TEST_OWNER_ID).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.firstName").value("George"))
			.andExpect(jsonPath("$.lastName").value("Franklin"))
			.andExpect(jsonPath("$.address").value("110 W. Liberty St."))
			.andExpect(jsonPath("$.city").value("Madison"))
			.andExpect(jsonPath("$.telephone").value("6085551023"))
			.andExpect(jsonPath("$.email").value("george@franklin.com"));
	}

	@Test
	void getOwnerNotFound() throws Exception {
		given(ownerRepository.findById(999)).willReturn(Optional.empty());
		mockMvc.perform(get("/api/v1/owners/999").accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
	}

	@Test
	void createOwner() throws Exception {
		mockMvc.perform(post("/api/v1/owners").contentType(MediaType.APPLICATION_JSON)
			.content(
					"""
							{"firstName":"Joe","lastName":"Bloggs","address":"123 Caramel Street","city":"London","telephone":"1316761638","email":"joe@bloggs.com"}"""))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.firstName").value("Joe"))
			.andExpect(jsonPath("$.email").value("joe@bloggs.com"));
	}

	@Test
	void createOwnerWithoutEmail() throws Exception {
		mockMvc.perform(post("/api/v1/owners").contentType(MediaType.APPLICATION_JSON)
			.content(
					"""
							{"firstName":"Joe","lastName":"Bloggs","address":"123 Caramel Street","city":"London","telephone":"1316761638"}"""))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.firstName").value("Joe"));
	}

	@Test
	void createOwnerValidationErrors() throws Exception {
		mockMvc.perform(post("/api/v1/owners").contentType(MediaType.APPLICATION_JSON).content("""
				{"firstName":"","lastName":"","address":"","city":"","telephone":""}"""))
			.andExpect(status().isBadRequest());
	}

	@Test
	void updateOwner() throws Exception {
		mockMvc.perform(put("/api/v1/owners/{ownerId}", TEST_OWNER_ID).contentType(MediaType.APPLICATION_JSON)
			.content(
					"""
							{"firstName":"Joe","lastName":"Bloggs","address":"123 Caramel Street","city":"London","telephone":"1616291589"}"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.firstName").value("Joe"));
	}

	@Test
	void updateOwnerNotFound() throws Exception {
		given(ownerRepository.findById(999)).willReturn(Optional.empty());
		mockMvc.perform(put("/api/v1/owners/999").contentType(MediaType.APPLICATION_JSON)
			.content(
					"""
							{"firstName":"Joe","lastName":"Bloggs","address":"123 Caramel Street","city":"London","telephone":"1616291589"}"""))
			.andExpect(status().isNotFound());
	}

	@Test
	void deleteOwner() throws Exception {
		mockMvc.perform(delete("/api/v1/owners/{ownerId}", TEST_OWNER_ID)).andExpect(status().isNoContent());
	}

	@Test
	void deleteOwnerNotFound() throws Exception {
		given(ownerRepository.existsById(999)).willReturn(false);
		mockMvc.perform(delete("/api/v1/owners/999")).andExpect(status().isNotFound());
	}

}