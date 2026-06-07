package org.springframework.samples.petclinic.rest.dto

import org.springframework.data.domain.Page

data class PagedResultDto<T : Any>(
    val content: List<T>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int
) {
    companion object {
        fun <E : Any, D : Any> from(page: Page<E>, mapper: (E) -> D): PagedResultDto<D> {
            return PagedResultDto(
                content = page.content.map(mapper),
                pageNumber = page.number + 1,
                pageSize = page.size,
                totalElements = page.totalElements,
                totalPages = page.totalPages
            )
        }
    }
}