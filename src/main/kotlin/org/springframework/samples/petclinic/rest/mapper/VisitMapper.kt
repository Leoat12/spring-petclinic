package org.springframework.samples.petclinic.rest.mapper

import org.springframework.samples.petclinic.owner.Visit
import org.springframework.samples.petclinic.rest.dto.VisitDto
import org.springframework.stereotype.Component

@Component
class VisitMapper {

    fun toDto(visit: Visit): VisitDto {
        return VisitDto(
            id = visit.id,
            date = visit.date,
            description = visit.description
        )
    }

}