package org.springframework.samples.petclinic.feedback

interface FeedbackRepository {

	fun save(feedback: Feedback): Feedback

	fun findAll(): List<Feedback>

}