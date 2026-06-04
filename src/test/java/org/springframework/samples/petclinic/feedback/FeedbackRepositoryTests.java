package org.springframework.samples.petclinic.feedback;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(JdbcClientFeedbackRepository.class)
class FeedbackRepositoryTests {

	@Autowired
	protected FeedbackRepository feedbackRepository;

	@Test
	@Transactional
	void shouldSaveFeedback() {
		Feedback feedback = new Feedback();
		feedback.setName("John Doe");
		feedback.setEmail("john@example.com");
		feedback.setMessage("Great clinic!");
		Feedback saved = feedbackRepository.save(feedback);
		assertThat(saved.getId()).isNotNull();
		assertThat(saved.getName()).isEqualTo("John Doe");
		assertThat(saved.getEmail()).isEqualTo("john@example.com");
		assertThat(saved.getMessage()).isEqualTo("Great clinic!");
		assertThat(saved.getCreatedAt()).isNotNull();
	}

	@Test
	@Transactional
	void shouldSaveFeedbackWithoutEmail() {
		Feedback feedback = new Feedback();
		feedback.setName("Jane Doe");
		feedback.setMessage("Very helpful staff.");
		Feedback saved = feedbackRepository.save(feedback);
		assertThat(saved.getId()).isNotNull();
		assertThat(saved.getEmail()).isNull();
	}

	@Test
	@Transactional
	void shouldFindAllFeedback() {
		Feedback feedback1 = new Feedback();
		feedback1.setName("Alice");
		feedback1.setEmail("alice@example.com");
		feedback1.setMessage("Nice service");
		feedbackRepository.save(feedback1);

		Feedback feedback2 = new Feedback();
		feedback2.setName("Bob");
		feedback2.setMessage("Good experience");
		feedbackRepository.save(feedback2);

		List<Feedback> allFeedback = feedbackRepository.findAll();
		assertThat(allFeedback).hasSizeGreaterThanOrEqualTo(2);
	}

	@Test
	@Transactional
	void shouldFindAllOrderedByCreatedAtDesc() {
		Feedback feedback1 = new Feedback();
		feedback1.setName("First");
		feedback1.setMessage("First message");
		feedbackRepository.save(feedback1);

		Feedback feedback2 = new Feedback();
		feedback2.setName("Second");
		feedback2.setMessage("Second message");
		feedbackRepository.save(feedback2);

		List<Feedback> allFeedback = feedbackRepository.findAll();
		assertThat(allFeedback).isNotEmpty();
		assertThat(allFeedback.get(0).getName()).isEqualTo("Second");
	}

}