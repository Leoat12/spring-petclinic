package org.springframework.samples.petclinic.feedback

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.Transactional

@DataJdbcTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(JdbcClientFeedbackRepository::class)
class FeedbackRepositoryTests {

    @Autowired
    protected lateinit var feedbackRepository: FeedbackRepository

    @Test
    @Transactional
    fun shouldSaveFeedback() {
        val feedback = Feedback()
        feedback.name = "John Doe"
        feedback.email = "john@example.com"
        feedback.message = "Great clinic!"
        val saved = feedbackRepository.save(feedback)
        assertThat(saved.id).isNotNull()
        assertThat(saved.name).isEqualTo("John Doe")
        assertThat(saved.email).isEqualTo("john@example.com")
        assertThat(saved.message).isEqualTo("Great clinic!")
        assertThat(saved.createdAt).isNotNull()
    }

    @Test
    @Transactional
    fun shouldSaveFeedbackWithoutEmail() {
        val feedback = Feedback()
        feedback.name = "Jane Doe"
        feedback.message = "Very helpful staff."
        val saved = feedbackRepository.save(feedback)
        assertThat(saved.id).isNotNull()
        assertThat(saved.email).isNull()
    }

    @Test
    @Transactional
    fun shouldFindAllFeedback() {
        val feedback1 = Feedback()
        feedback1.name = "Alice"
        feedback1.email = "alice@example.com"
        feedback1.message = "Nice service"
        feedbackRepository.save(feedback1)

        val feedback2 = Feedback()
        feedback2.name = "Bob"
        feedback2.message = "Good experience"
        feedbackRepository.save(feedback2)

        val allFeedback = feedbackRepository.findAll()
        assertThat(allFeedback).hasSizeGreaterThanOrEqualTo(2)
    }

    @Test
    @Transactional
    fun shouldFindAllOrderedByCreatedAtDesc() {
        val feedback1 = Feedback()
        feedback1.name = "First"
        feedback1.message = "First message"
        feedbackRepository.save(feedback1)

        val feedback2 = Feedback()
        feedback2.name = "Second"
        feedback2.message = "Second message"
        feedbackRepository.save(feedback2)

        val allFeedback = feedbackRepository.findAll()
        assertThat(allFeedback).isNotEmpty()
        assertThat(allFeedback[0].name).isEqualTo("Second")
    }

}