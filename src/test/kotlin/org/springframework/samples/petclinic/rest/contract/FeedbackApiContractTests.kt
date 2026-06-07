package org.springframework.samples.petclinic.rest.contract

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

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class FeedbackApiContractTests {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun createFeedbackReturnsCreatedWithLocation() {
        val dto = FeedbackCreateDto("Contract Test", "contract@test.com", "Contract test message")
        val response: ResponseEntity<FeedbackDto> = restTemplate.postForEntity("/api/v1/feedback", dto, FeedbackDto::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        assertThat(response.body).isNotNull()
        assertThat(response.body!!.id).isNotNull()
        assertThat(response.body!!.name).isEqualTo("Contract Test")
        assertThat(response.body!!.email).isEqualTo("contract@test.com")
        assertThat(response.body!!.message).isEqualTo("Contract test message")
        assertThat(response.body!!.createdAt).isNotNull()
    }

    @Test
    fun listFeedbackReturnsList() {
        val dto = FeedbackCreateDto("List Contract", "list@test.com", "List contract test")
        restTemplate.postForEntity("/api/v1/feedback", dto, FeedbackDto::class.java)

        val response: ResponseEntity<List<FeedbackDto>> = restTemplate.exchange(
            org.springframework.http.RequestEntity.get("/api/v1/feedback").build(),
            object : org.springframework.core.ParameterizedTypeReference<List<FeedbackDto>>() {}
        )
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull()
    }

    @Test
    fun createFeedbackValidatesRequiredFields() {
        val blankDto = FeedbackCreateDto("", null, "")
        val response: ResponseEntity<String> = restTemplate.postForEntity("/api/v1/feedback", blankDto, String::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun createFeedbackValidatesEmailFormat() {
        val invalidEmailDto = FeedbackCreateDto("Test", "not-an-email", "Hello")
        val response: ResponseEntity<String> = restTemplate.postForEntity("/api/v1/feedback", invalidEmailDto, String::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun createFeedbackValidatesMessageLength() {
        val longMessage = "a".repeat(2001)
        val dto = FeedbackCreateDto("Test", null, longMessage)
        val response: ResponseEntity<String> = restTemplate.postForEntity("/api/v1/feedback", dto, String::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun createFeedbackAllowsNullEmail() {
        val dto = FeedbackCreateDto("No Email", null, "Hello world")
        val response: ResponseEntity<FeedbackDto> = restTemplate.postForEntity("/api/v1/feedback", dto, FeedbackDto::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        assertThat(response.body).isNotNull()
        assertThat(response.body!!.email).isNull()
    }

}