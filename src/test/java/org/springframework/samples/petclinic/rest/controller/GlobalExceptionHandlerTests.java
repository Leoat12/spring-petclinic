package org.springframework.samples.petclinic.rest.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.rest.mapper.OwnerMapper;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OwnerRestController.class)
class GlobalExceptionHandlerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OwnerRepository ownerRepository;

	@MockitoBean
	private OwnerMapper ownerMapper;

	@Test
	void shouldReturnNotFoundResponseForResourceNotFoundException() throws Exception {
		given(ownerRepository.findById(999)).willReturn(Optional.empty());

		MvcResult result = mockMvc.perform(get("/api/v1/owners/999").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status").value(404))
			.andExpect(jsonPath("$.error").value("Not Found"))
			.andExpect(jsonPath("$.message").value("Owner not found with id: 999"))
			.andExpect(jsonPath("$.path").value("/api/v1/owners/999"))
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andReturn();

		assertThat(result.getResponse().getContentType()).isNotNull();
	}

	@Test
	void shouldReturnValidationErrorForInvalidInput() throws Exception {
		mockMvc
			.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/v1/owners")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"firstName\":\"\",\"lastName\":\"\",\"address\":\"\",\"city\":\"\",\"telephone\":\"abc\"}"))
			.andExpect(status().isBadRequest());
	}

	@Test
	void shouldReturnNotFoundForInvalidUrl() throws Exception {
		mockMvc.perform(get("/api/v1/nonexistent").accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
	}

}