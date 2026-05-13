package org.springframework.samples.petclinic.rest.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.owner.PetTypeRepository;
import org.springframework.samples.petclinic.rest.dto.PetDto;
import org.springframework.samples.petclinic.rest.dto.PetTypeDto;
import org.springframework.samples.petclinic.rest.mapper.PetMapper;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PetRestController.class)
class PetRestControllerTests {

	private static final int TEST_OWNER_ID = 1;

	private static final int TEST_PET_ID = 1;

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OwnerRepository ownerRepository;

	@MockitoBean
	private PetTypeRepository petTypeRepository;

	@MockitoBean
	private PetMapper petMapper;

	private Owner ownerWithPet() {
		Owner owner = new Owner();
		owner.setId(TEST_OWNER_ID);
		owner.setFirstName("George");
		owner.setLastName("Franklin");
		owner.setAddress("110 W. Liberty St.");
		owner.setCity("Madison");
		owner.setTelephone("6085551023");
		PetType dog = new PetType();
		dog.setId(1);
		dog.setName("dog");
		Pet pet = new Pet();
		pet.setName("Max");
		pet.setBirthDate(LocalDate.of(2020, 1, 1));
		pet.setType(dog);
		owner.addPet(pet);
		pet.setId(TEST_PET_ID);
		return owner;
	}

	@BeforeEach
	void setup() {
		PetType dog = new PetType();
		dog.setId(1);
		dog.setName("dog");
		given(petTypeRepository.findById(1)).willReturn(Optional.of(dog));
		given(ownerRepository.findById(TEST_OWNER_ID)).willReturn(Optional.of(ownerWithPet()));
		given(ownerRepository.save(any(Owner.class))).willAnswer(invocation -> invocation.getArgument(0));
		given(petMapper.toDto(any(Pet.class))).willAnswer(invocation -> {
			Pet pet = invocation.getArgument(0);
			return new PetDto(pet.getId(), pet.getName(), pet.getBirthDate(),
					new PetTypeDto(pet.getType().getId(), pet.getType().getName()), List.of());
		});
	}

	@Test
	void listPets() throws Exception {
		mockMvc.perform(get("/api/v1/owners/{ownerId}/pets", TEST_OWNER_ID).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$[0].name").value("Max"));
	}

	@Test
	void listPetsOwnerNotFound() throws Exception {
		given(ownerRepository.findById(999)).willReturn(Optional.empty());
		mockMvc.perform(get("/api/v1/owners/999/pets").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}

	@Test
	void getPet() throws Exception {
		mockMvc
			.perform(get("/api/v1/owners/{ownerId}/pets/{petId}", TEST_OWNER_ID, TEST_PET_ID)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("Max"));
	}

	@Test
	void getPetNotFound() throws Exception {
		mockMvc.perform(get("/api/v1/owners/{ownerId}/pets/999", TEST_OWNER_ID).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}

	@Test
	void createPet() throws Exception {
		given(petMapper.toDto(any(Pet.class))).willAnswer(invocation -> {
			Pet pet = invocation.getArgument(0);
			return new PetDto(2, pet.getName(), pet.getBirthDate(), new PetTypeDto(1, "dog"), List.of());
		});
		mockMvc
			.perform(post("/api/v1/owners/{ownerId}/pets", TEST_OWNER_ID).contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"name":"Buddy","birthDate":"2023-01-01","typeId":1}"""))
			.andExpect(status().isCreated());
	}

	@Test
	void createPetInvalidData() throws Exception {
		mockMvc
			.perform(post("/api/v1/owners/{ownerId}/pets", TEST_OWNER_ID).contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"name":"","birthDate":null,"typeId":null}"""))
			.andExpect(status().isBadRequest());
	}

	@Test
	void updatePet() throws Exception {
		mockMvc
			.perform(put("/api/v1/owners/{ownerId}/pets/{petId}", TEST_OWNER_ID, TEST_PET_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"name":"Max Updated","birthDate":"2020-01-01","typeId":1}"""))
			.andExpect(status().isOk());
	}

	@Test
	void deletePet() throws Exception {
		mockMvc.perform(delete("/api/v1/owners/{ownerId}/pets/{petId}", TEST_OWNER_ID, TEST_PET_ID))
			.andExpect(status().isNoContent());
	}

	@Test
	void deletePetNotFound() throws Exception {
		mockMvc.perform(delete("/api/v1/owners/{ownerId}/pets/999", TEST_OWNER_ID)).andExpect(status().isNotFound());
	}

}