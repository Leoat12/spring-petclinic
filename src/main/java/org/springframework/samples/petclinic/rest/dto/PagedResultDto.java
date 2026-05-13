package org.springframework.samples.petclinic.rest.dto;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;

public record PagedResultDto<T>(List<T> content, int pageNumber, int pageSize, long totalElements, int totalPages) {

	public static <E, D> PagedResultDto<D> from(Page<E> page, Function<E, D> mapper) {
		return new PagedResultDto<>(page.getContent().stream().map(mapper).toList(), page.getNumber() + 1,
				page.getSize(), page.getTotalElements(), page.getTotalPages());
	}

}