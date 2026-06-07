package org.springframework.samples.petclinic.feedback

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.model
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.view

@WebMvcTest(FeedbackController::class)
class FeedbackControllerTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var feedbackRepository: FeedbackRepository

    private fun sampleFeedback(): Feedback {
        val feedback = Feedback()
        feedback.id = 1
        feedback.name = "John Doe"
        feedback.email = "john@example.com"
        feedback.message = "Great clinic!"
        return feedback
    }

    @BeforeEach
    fun setup() {
        given(feedbackRepository.findAll()).willReturn(listOf(sampleFeedback()))
        given(feedbackRepository.save(any<Feedback>())).willAnswer { invocation ->
            val f = invocation.getArgument(0) as Feedback
            f.id = 1
            f
        }
    }

    @Test
    fun initCreationForm() {
        mockMvc.perform(get("/feedback/new"))
            .andExpect(status().isOk)
            .andExpect(view().name("feedback/feedbackForm"))
            .andExpect(model().attributeExists("feedback"))
    }

    @Test
    fun processCreationFormSuccess() {
        mockMvc.perform(post("/feedback/new").param("name", "Jane Doe").param("message", "Great service!"))
            .andExpect(status().is3xxRedirection)
            .andExpect(view().name("redirect:/feedback/new"))
    }

    @Test
    fun processCreationFormWithErrors() {
        mockMvc.perform(post("/feedback/new").param("name", "").param("message", ""))
            .andExpect(status().isOk)
            .andExpect(view().name("feedback/feedbackForm"))
    }

    @Test
    fun showFeedbackList() {
        mockMvc.perform(get("/admin/feedback"))
            .andExpect(status().isOk)
            .andExpect(view().name("feedback/feedbackList"))
            .andExpect(model().attributeExists("feedbackList"))
    }

}