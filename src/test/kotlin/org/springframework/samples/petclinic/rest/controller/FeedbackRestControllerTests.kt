package org.springframework.samples.petclinic.rest.controller

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.samples.petclinic.feedback.Feedback
import org.springframework.samples.petclinic.feedback.FeedbackRepository
import org.springframework.samples.petclinic.rest.dto.FeedbackCreateDto
import org.springframework.samples.petclinic.rest.dto.FeedbackDto
import org.springframework.samples.petclinic.rest.mapper.FeedbackMapper
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@WebMvcTest(FeedbackRestController::class)
class FeedbackRestControllerTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var feedbackRepository: FeedbackRepository

    @MockitoBean
    private lateinit var feedbackMapper: FeedbackMapper

    private fun sampleFeedback(): Feedback {
        val feedback = Feedback()
        feedback.id = 1
        feedback.name = "John Doe"
        feedback.email = "john@example.com"
        feedback.message = "Great clinic!"
        feedback.createdAt = LocalDateTime.of(2025, 6, 1, 10, 0)
        return feedback
    }

    @BeforeEach
    fun setup() {
        val feedback = sampleFeedback()
        given(feedbackRepository.findAll()).willReturn(listOf(feedback))
        given(feedbackRepository.save(any<Feedback>())).willAnswer { invocation ->
            val f = invocation.getArgument(0) as Feedback
            if (f.id == null) {
                f.id = 2
            }
            f
        }
        given(feedbackMapper.toDto(any<Feedback>())).willAnswer { invocation ->
            val f = invocation.getArgument(0) as Feedback
            FeedbackDto(f.id, f.name, f.email, f.message, f.createdAt)
        }
        given(feedbackMapper.toDtoList(any<MutableList<Feedback>>())).willAnswer { invocation ->
            val list = invocation.getArgument(0) as List<*>
            list.map { f ->
                f as Feedback
                FeedbackDto(f.id, f.name, f.email, f.message, f.createdAt)
            }
        }
        given(feedbackMapper.toEntity(any<FeedbackCreateDto>())).willAnswer { invocation ->
            val dto = invocation.getArgument(0) as FeedbackCreateDto
            val f = Feedback()
            f.name = dto.name
            f.email = dto.email
            f.message = dto.message
            f
        }
    }

    @Test
    fun listFeedback() {
        mockMvc.perform(get("/api/v1/feedback").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("\$[0].name").value("John Doe"))
            .andExpect(jsonPath("\$[0].email").value("john@example.com"))
            .andExpect(jsonPath("\$[0].message").value("Great clinic!"))
    }

    @Test
    fun createFeedback() {
        mockMvc.perform(
            post("/api/v1/feedback").contentType(MediaType.APPLICATION_JSON).content(
                """{"name":"Jane Doe","email":"jane@example.com","message":"Very helpful"}"""
            )
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("Jane Doe"))
            .andExpect(jsonPath("$.email").value("jane@example.com"))
            .andExpect(jsonPath("$.message").value("Very helpful"))
    }

    @Test
    fun createFeedbackWithoutEmail() {
        mockMvc.perform(
            post("/api/v1/feedback").contentType(MediaType.APPLICATION_JSON).content(
                """{"name":"Jane Doe","message":"Very helpful"}"""
            )
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("Jane Doe"))
    }

    @Test
    fun createFeedbackValidationErrors() {
        mockMvc.perform(
            post("/api/v1/feedback").contentType(MediaType.APPLICATION_JSON).content(
                """{"name":"","message":""}"""
            )
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun createFeedbackMessageTooLong() {
        val longMessage = "a".repeat(2001)
        mockMvc.perform(
            post("/api/v1/feedback").contentType(MediaType.APPLICATION_JSON)
                .content("""{"name":"Test","message":"$longMessage"}""")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun createFeedbackInvalidEmail() {
        mockMvc.perform(
            post("/api/v1/feedback").contentType(MediaType.APPLICATION_JSON).content(
                """{"name":"Test","email":"not-an-email","message":"Hello"}"""
            )
        ).andExpect(status().isBadRequest)
    }

}