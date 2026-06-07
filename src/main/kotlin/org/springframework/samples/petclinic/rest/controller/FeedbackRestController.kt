package org.springframework.samples.petclinic.rest.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.samples.petclinic.feedback.FeedbackRepository
import org.springframework.samples.petclinic.rest.dto.FeedbackCreateDto
import org.springframework.samples.petclinic.rest.dto.FeedbackDto
import org.springframework.samples.petclinic.rest.mapper.FeedbackMapper
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder

@RestController
@RequestMapping("/api/v1/feedback")
class FeedbackRestController(
    private val feedbackRepository: FeedbackRepository,
    private val feedbackMapper: FeedbackMapper
) {

    @GetMapping
    fun list(): List<FeedbackDto> {
        return feedbackMapper.toDtoList(feedbackRepository.findAll())
    }

    @PostMapping
    fun create(@Valid @RequestBody dto: FeedbackCreateDto, uriBuilder: UriComponentsBuilder): ResponseEntity<FeedbackDto> {
        val feedback = feedbackMapper.toEntity(dto)
        val saved = feedbackRepository.save(feedback)
        val uri = uriBuilder.path("/api/v1/feedback/{id}").buildAndExpand(saved.id).toUri()
        return ResponseEntity.created(uri).body(feedbackMapper.toDto(saved))
    }

}