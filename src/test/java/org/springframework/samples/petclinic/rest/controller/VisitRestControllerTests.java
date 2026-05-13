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
import org.springframework.samples.petclinic.owner.Visit;
import org.springframework.samples.petclinic.rest.dto.VisitDto;
import org.springframework.samples.petclinic.rest.mapper.VisitMapper;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VisitRestController.class)
class VisitRestControllerTests {

	private static final int TEST_OWNER_ID = 1;

	private static final int TEST_PET_ID = 1;

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OwnerRepository ownerRepository;

	@MockitoBean
	private VisitMapper visitMapper;

	private Owner ownerWithPetAndVisit() {
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
		Visit visit = new Visit();
		visit.setDate(LocalDate.of(2024, 1, 15));
		visit.setDescription("rabies shot");
		pet.addVisit(visit);
		visit.setId(1);
		return owner;
	}

	@BeforeEach
	void setup() {
		given(ownerRepository.findById(TEST_OWNER_ID)).willReturn(Optional.of(ownerWithPetAndVisit()));
		given(ownerRepository.save(any(Owner.class))).willAnswer(invocation -> invocation.getArgument(0));
		given(visitMapper.toDto(any(Visit.class))).willAnswer(invocation -> {
			Visit visit = invocation.getArgument(0);
			return new VisitDto(visit.getId(), visit.getDate(), visit.getDescription());
		});
	}

	@Test
	void listVisits() throws Exception {
		mockMvc
			.perform(get("/api/v1/owners/{ownerId}/pets/{petId}/visits", TEST_OWNER_ID, TEST_PET_ID)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$[0].description").value("rabies shot"));
	}

	@Test
	void listVisitsOwnerNotFound() throws Exception {
		given(ownerRepository.findById(999)).willReturn(Optional.empty());
		mockMvc.perform(get("/api/v1/owners/999/pets/1/visits").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}

	@Test
	void listVisitsPetNotFound() throws Exception {
		mockMvc
			.perform(get("/api/v1/owners/{ownerId}/pets/999/visits", TEST_OWNER_ID).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}

	@Test
	void createVisit() throws Exception {
		given(visitMapper.toDto(any(Visit.class))).willAnswer(invocation -> {
			Visit visit = invocation.getArgument(0);
			return new VisitDto(2, visit.getDate(), visit.getDescription());
		});
		mockMvc
			.perform(post("/api/v1/owners/{ownerId}/pets/{petId}/visits", TEST_OWNER_ID, TEST_PET_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"date":"2024-03-01","description":"checkup"}"""))
			.andExpect(status().isCreated());
	}

	@Test
	void createVisitBlankDescription() throws Exception {
		mockMvc
			.perform(post("/api/v1/owners/{ownerId}/pets/{petId}/visits", TEST_OWNER_ID, TEST_PET_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"date":"2024-03-01","description":""}"""))
			.andExpect(status().isBadRequest());
	}

}