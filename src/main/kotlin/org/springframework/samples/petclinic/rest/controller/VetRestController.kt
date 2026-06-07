package org.springframework.samples.petclinic.rest.controller

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.samples.petclinic.rest.ResourceNotFoundException
import org.springframework.samples.petclinic.rest.dto.PagedResultDto
import org.springframework.samples.petclinic.rest.dto.VetDto
import org.springframework.samples.petclinic.rest.mapper.VetMapper
import org.springframework.samples.petclinic.vet.Vet
import org.springframework.samples.petclinic.vet.VetRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/vets")
class VetRestController(
    private val vetRepository: VetRepository,
    private val vetMapper: VetMapper
) {

    @GetMapping
    fun list(@RequestParam(defaultValue = "1") page: Int, @RequestParam(defaultValue = "20") size: Int): PagedResultDto<VetDto> {
        val pageable: Pageable = PageRequest.of(page - 1, size, Sort.by("lastName", "firstName"))
        val vets: Page<Vet> = vetRepository.findAll(pageable)
        return PagedResultDto.from(vets, vetMapper::toDto)
    }

    @GetMapping("/{vetId}")
    fun detail(@PathVariable vetId: Int): VetDto {
        val vet = vetRepository.findById(vetId)
            .orElseThrow { ResourceNotFoundException("Vet not found with id: $vetId") }
        return vetMapper.toDto(vet)
    }

}