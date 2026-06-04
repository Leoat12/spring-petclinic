package org.springframework.samples.petclinic.rest.controller;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.feedback.Feedback;
import org.springframework.samples.petclinic.feedback.FeedbackRepository;
import org.springframework.samples.petclinic.rest.dto.FeedbackCreateDto;
import org.springframework.samples.petclinic.rest.dto.FeedbackDto;
import org.springframework.samples.petclinic.rest.mapper.FeedbackMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/feedback")
class FeedbackRestController {

	private final FeedbackRepository feedbackRepository;

	private final FeedbackMapper feedbackMapper;

	public FeedbackRestController(FeedbackRepository feedbackRepository, FeedbackMapper feedbackMapper) {
		this.feedbackRepository = feedbackRepository;
		this.feedbackMapper = feedbackMapper;
	}

	@GetMapping
	List<FeedbackDto> list() {
		return feedbackMapper.toDtoList(feedbackRepository.findAll());
	}

	@PostMapping
	ResponseEntity<FeedbackDto> create(@Valid @RequestBody FeedbackCreateDto dto, UriComponentsBuilder uriBuilder) {
		Feedback feedback = feedbackMapper.toEntity(dto);
		Feedback saved = feedbackRepository.save(feedback);
		URI uri = uriBuilder.path("/api/v1/feedback/{id}").buildAndExpand(saved.getId()).toUri();
		return ResponseEntity.created(uri).body(feedbackMapper.toDto(saved));
	}

}