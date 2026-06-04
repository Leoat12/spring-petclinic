package org.springframework.samples.petclinic.rest.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.feedback.Feedback;
import org.springframework.samples.petclinic.feedback.FeedbackRepository;
import org.springframework.samples.petclinic.rest.dto.FeedbackDto;
import org.springframework.samples.petclinic.rest.mapper.FeedbackMapper;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FeedbackRestController.class)
class FeedbackRestControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private FeedbackRepository feedbackRepository;

	@MockitoBean
	private FeedbackMapper feedbackMapper;

	private Feedback sampleFeedback() {
		Feedback feedback = new Feedback();
		feedback.setId(1);
		feedback.setName("John Doe");
		feedback.setEmail("john@example.com");
		feedback.setMessage("Great clinic!");
		feedback.setCreatedAt(LocalDateTime.of(2025, 6, 1, 10, 0));
		return feedback;
	}

	@BeforeEach
	void setup() {
		Feedback feedback = sampleFeedback();
		given(feedbackRepository.findAll()).willReturn(List.of(feedback));
		given(feedbackRepository.save(any(Feedback.class))).willAnswer(invocation -> {
			Feedback f = invocation.getArgument(0);
			if (f.getId() == null) {
				f.setId(2);
			}
			return f;
		});
		given(feedbackMapper.toDto(any(Feedback.class))).willAnswer(invocation -> {
			Feedback f = invocation.getArgument(0);
			return new FeedbackDto(f.getId(), f.getName(), f.getEmail(), f.getMessage(), f.getCreatedAt());
		});
		given(feedbackMapper.toDtoList(any(List.class))).willAnswer(invocation -> {
			List<Feedback> list = invocation.getArgument(0);
			return list.stream()
				.map(f -> new FeedbackDto(f.getId(), f.getName(), f.getEmail(), f.getMessage(), f.getCreatedAt()))
				.toList();
		});
		given(feedbackMapper.toEntity(any(org.springframework.samples.petclinic.rest.dto.FeedbackCreateDto.class)))
			.willAnswer(invocation -> {
				org.springframework.samples.petclinic.rest.dto.FeedbackCreateDto dto = invocation.getArgument(0);
				Feedback f = new Feedback();
				f.setName(dto.name());
				f.setEmail(dto.email());
				f.setMessage(dto.message());
				return f;
			});
	}

	@Test
	void listFeedback() throws Exception {
		mockMvc.perform(get("/api/v1/feedback").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].name").value("John Doe"))
			.andExpect(jsonPath("$[0].email").value("john@example.com"))
			.andExpect(jsonPath("$[0].message").value("Great clinic!"));
	}

	@Test
	void createFeedback() throws Exception {
		mockMvc.perform(post("/api/v1/feedback").contentType(MediaType.APPLICATION_JSON).content("""
				{"name":"Jane Doe","email":"jane@example.com","message":"Very helpful"}"""))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.name").value("Jane Doe"))
			.andExpect(jsonPath("$.email").value("jane@example.com"))
			.andExpect(jsonPath("$.message").value("Very helpful"));
	}

	@Test
	void createFeedbackWithoutEmail() throws Exception {
		mockMvc.perform(post("/api/v1/feedback").contentType(MediaType.APPLICATION_JSON).content("""
				{"name":"Jane Doe","message":"Very helpful"}"""))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.name").value("Jane Doe"));
	}

	@Test
	void createFeedbackValidationErrors() throws Exception {
		mockMvc.perform(post("/api/v1/feedback").contentType(MediaType.APPLICATION_JSON).content("""
				{"name":"","message":""}""")).andExpect(status().isBadRequest());
	}

	@Test
	void createFeedbackMessageTooLong() throws Exception {
		String longMessage = "a".repeat(2001);
		mockMvc
			.perform(post("/api/v1/feedback").contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"Test\",\"message\":\"" + longMessage + "\"}"))
			.andExpect(status().isBadRequest());
	}

	@Test
	void createFeedbackInvalidEmail() throws Exception {
		mockMvc.perform(post("/api/v1/feedback").contentType(MediaType.APPLICATION_JSON).content("""
				{"name":"Test","email":"not-an-email","message":"Hello"}""")).andExpect(status().isBadRequest());
	}

}