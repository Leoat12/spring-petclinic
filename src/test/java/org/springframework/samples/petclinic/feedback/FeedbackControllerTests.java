package org.springframework.samples.petclinic.feedback;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.samples.petclinic.feedback.Feedback;
import org.springframework.samples.petclinic.feedback.FeedbackController;
import org.springframework.samples.petclinic.feedback.FeedbackRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(FeedbackController.class)
class FeedbackControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private FeedbackRepository feedbackRepository;

	private Feedback sampleFeedback() {
		Feedback feedback = new Feedback();
		feedback.setId(1);
		feedback.setName("John Doe");
		feedback.setEmail("john@example.com");
		feedback.setMessage("Great clinic!");
		return feedback;
	}

	@BeforeEach
	void setup() {
		given(feedbackRepository.findAll()).willReturn(List.of(sampleFeedback()));
		given(feedbackRepository.save(any(Feedback.class))).willAnswer(invocation -> {
			Feedback f = invocation.getArgument(0);
			f.setId(1);
			return f;
		});
	}

	@Test
	void initCreationForm() throws Exception {
		mockMvc.perform(get("/feedback/new"))
			.andExpect(status().isOk())
			.andExpect(view().name("feedback/feedbackForm"))
			.andExpect(model().attributeExists("feedback"));
	}

	@Test
	void processCreationFormSuccess() throws Exception {
		mockMvc.perform(post("/feedback/new").param("name", "Jane Doe").param("message", "Great service!"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/feedback/new"));
	}

	@Test
	void processCreationFormWithErrors() throws Exception {
		mockMvc.perform(post("/feedback/new").param("name", "").param("message", ""))
			.andExpect(status().isOk())
			.andExpect(view().name("feedback/feedbackForm"));
	}

	@Test
	void showFeedbackList() throws Exception {
		mockMvc.perform(get("/admin/feedback"))
			.andExpect(status().isOk())
			.andExpect(view().name("feedback/feedbackList"))
			.andExpect(model().attributeExists("feedbackList"));
	}

}