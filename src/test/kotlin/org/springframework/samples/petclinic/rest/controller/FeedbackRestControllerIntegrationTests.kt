package org.springframework.samples.petclinic.rest.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.TestRestTemplate
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.samples.petclinic.rest.dto.FeedbackCreateDto
import org.springframework.samples.petclinic.rest.dto.FeedbackDto
import org.springframework.test.annotation.DirtiesContext

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FeedbackRestControllerIntegrationTests {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun shouldCreateFeedback() {
        val dto = FeedbackCreateDto("Jane Doe", "jane@example.com", "Great service!")
        val response: ResponseEntity<FeedbackDto> = restTemplate.postForEntity("/api/v1/feedback", dto, FeedbackDto::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        assertThat(response.body).isNotNull()
        assertThat(response.body!!.id).isNotNull()
        assertThat(response.body!!.name).isEqualTo("Jane Doe")
        assertThat(response.body!!.email).isEqualTo("jane@example.com")
        assertThat(response.body!!.message).isEqualTo("Great service!")
    }

    @Test
    fun shouldCreateFeedbackWithoutEmail() {
        val dto = FeedbackCreateDto("Bob Smith", null, "Nice experience")
        val response: ResponseEntity<FeedbackDto> = restTemplate.postForEntity("/api/v1/feedback", dto, FeedbackDto::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        assertThat(response.body).isNotNull()
        assertThat(response.body!!.email).isNull()
    }

    @Test
    fun shouldRejectCreateFeedbackWithBlankName() {
        val dto = FeedbackCreateDto("", "test@example.com", "Hello")
        val response: ResponseEntity<String> = restTemplate.postForEntity("/api/v1/feedback", dto, String::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun shouldRejectCreateFeedbackWithBlankMessage() {
        val dto = FeedbackCreateDto("Test", "test@example.com", "")
        val response: ResponseEntity<String> = restTemplate.postForEntity("/api/v1/feedback", dto, String::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun shouldRejectCreateFeedbackWithInvalidEmail() {
        val dto = FeedbackCreateDto("Test", "not-an-email", "Hello")
        val response: ResponseEntity<String> = restTemplate.postForEntity("/api/v1/feedback", dto, String::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun shouldListFeedback() {
        val dto = FeedbackCreateDto("Alice", "alice@example.com", "First feedback")
        restTemplate.postForEntity("/api/v1/feedback", dto, FeedbackDto::class.java)

        val response: ResponseEntity<List<FeedbackDto>> = restTemplate.exchange(
            org.springframework.http.RequestEntity.get("/api/v1/feedback").build(),
            object : org.springframework.core.ParameterizedTypeReference<List<FeedbackDto>>() {}
        )
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull()
        assertThat(response.body!!).hasSizeGreaterThanOrEqualTo(1)
    }

}