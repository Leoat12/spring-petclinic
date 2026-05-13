package org.springframework.samples.petclinic.rest.controller;

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
import org.springframework.samples.petclinic.rest.mapper.VetMapper;
import org.springframework.samples.petclinic.vet.Specialty;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VetRestController.class)
class VetRestControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private VetRepository vetRepository;

	@MockitoBean
	private VetMapper vetMapper;

	private Vet sampleVet() {
		Vet vet = new Vet();
		vet.setId(1);
		vet.setFirstName("James");
		vet.setLastName("Carter");
		return vet;
	}

	@BeforeEach
	void setup() {
		Vet vet = sampleVet();
		given(vetRepository.findAll(any(Pageable.class))).willReturn(new PageImpl<>(List.of(vet)));
		given(vetRepository.findById(1)).willReturn(Optional.of(vet));
		given(vetMapper.toDto(any(Vet.class))).willAnswer(invocation -> {
			Vet v = invocation.getArgument(0);
			return new org.springframework.samples.petclinic.rest.dto.VetDto(v.getId(), v.getFirstName(),
					v.getLastName(), List.of());
		});
	}

	@Test
	void listVets() throws Exception {
		mockMvc.perform(get("/api/v1/vets").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.content[0].firstName").value("James"))
			.andExpect(jsonPath("$.totalElements").value(1));
	}

	@Test
	void getVet() throws Exception {
		mockMvc.perform(get("/api/v1/vets/{vetId}", 1).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.firstName").value("James"))
			.andExpect(jsonPath("$.lastName").value("Carter"));
	}

	@Test
	void getVetNotFound() throws Exception {
		given(vetRepository.findById(999)).willReturn(Optional.empty());
		mockMvc.perform(get("/api/v1/vets/999").accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
	}

}