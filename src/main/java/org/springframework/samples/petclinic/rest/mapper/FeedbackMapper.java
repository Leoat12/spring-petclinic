package org.springframework.samples.petclinic.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.springframework.samples.petclinic.feedback.Feedback;
import org.springframework.samples.petclinic.rest.dto.FeedbackCreateDto;
import org.springframework.samples.petclinic.rest.dto.FeedbackDto;

@Mapper(componentModel = "spring")
public interface FeedbackMapper {

	FeedbackDto toDto(Feedback feedback);

	List<FeedbackDto> toDtoList(List<Feedback> feedbackList);

	Feedback toEntity(FeedbackCreateDto dto);

}