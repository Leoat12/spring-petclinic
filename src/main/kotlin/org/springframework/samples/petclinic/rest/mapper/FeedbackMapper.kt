package org.springframework.samples.petclinic.rest.mapper

import org.springframework.samples.petclinic.feedback.Feedback
import org.springframework.samples.petclinic.rest.dto.FeedbackCreateDto
import org.springframework.samples.petclinic.rest.dto.FeedbackDto
import org.springframework.stereotype.Component

@Component
class FeedbackMapper {

    fun toDto(feedback: Feedback): FeedbackDto {
        return FeedbackDto(
            id = feedback.id,
            name = feedback.name,
            email = feedback.email,
            message = feedback.message,
            createdAt = feedback.createdAt
        )
    }

    fun toDtoList(feedbackList: List<Feedback>): List<FeedbackDto> {
        return feedbackList.map { toDto(it) }
    }

    fun toEntity(dto: FeedbackCreateDto): Feedback {
        val feedback = Feedback()
        feedback.name = dto.name
        feedback.email = dto.email
        feedback.message = dto.message
        return feedback
    }

}