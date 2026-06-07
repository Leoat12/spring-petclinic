package org.springframework.samples.petclinic.rest.controller

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.samples.petclinic.rest.dto.VetDto
import org.springframework.samples.petclinic.rest.mapper.VetMapper
import org.springframework.samples.petclinic.vet.Specialty
import org.springframework.samples.petclinic.vet.Vet
import org.springframework.samples.petclinic.vet.VetRepository
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.Optional

@WebMvcTest(VetRestController::class)
class VetRestControllerTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var vetRepository: VetRepository

    @MockitoBean
    private lateinit var vetMapper: VetMapper

    private fun sampleVet(): Vet {
        val vet = Vet()
        vet.id = 1
        vet.firstName = "James"
        vet.lastName = "Carter"
        return vet
    }

    @BeforeEach
    fun setup() {
        val vet = sampleVet()
        given(vetRepository.findAll(any<Pageable>())).willReturn(PageImpl(listOf(vet)))
        given(vetRepository.findById(1)).willReturn(Optional.of(vet))
        given(vetMapper.toDto(any<Vet>())).willAnswer { invocation ->
            val v = invocation.getArgument(0) as Vet
            VetDto(v.id, v.firstName, v.lastName, emptyList())
        }
    }

    @Test
    fun listVets() {
        mockMvc.perform(get("/api/v1/vets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").isArray)
            .andExpect(jsonPath("$.content[0].firstName").value("James"))
            .andExpect(jsonPath("$.totalElements").value(1))
    }

    @Test
    fun listVetsWithCustomPageSize() {
        mockMvc.perform(get("/api/v1/vets").param("page", "1").param("size", "5").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").isArray)
    }

    @Test
    fun getVet() {
        mockMvc.perform(get("/api/v1/vets/{vetId}", 1).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.firstName").value("James"))
            .andExpect(jsonPath("$.lastName").value("Carter"))
    }

    @Test
    fun getVetNotFound() {
        given(vetRepository.findById(999)).willReturn(Optional.empty())
        mockMvc.perform(get("/api/v1/vets/999").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound)
    }

}