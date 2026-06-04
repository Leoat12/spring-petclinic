package org.springframework.samples.petclinic.rest.dto;

import java.time.LocalDateTime;

public record FeedbackDto(Integer id, String name, String email, String message, LocalDateTime createdAt) {
}