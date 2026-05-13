package org.springframework.samples.petclinic.rest;

import java.time.LocalDateTime;

public record ApiError(int status, String error, String message, String path, LocalDateTime timestamp) {
}