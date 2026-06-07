package org.springframework.samples.petclinic.vet

import org.assertj.core.util.Lists
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledInNativeImage
import org.mockito.kotlin.any
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.test.context.aot.DisabledInAotMode
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.model
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.view

@WebMvcTest(VetController::class)
@DisabledInNativeImage
@DisabledInAotMode
class VetControllerTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var vets: VetRepository

    private fun james(): Vet {
        val james = Vet()
        james.firstName = "James"
        james.lastName = "Carter"
        james.id = 1
        return james
    }

    private fun helen(): Vet {
        val helen = Vet()
        helen.firstName = "Helen"
        helen.lastName = "Leary"
        helen.id = 2
        val radiology = Specialty()
        radiology.id = 1
        radiology.name = "radiology"
        helen.addSpecialty(radiology)
        return helen
    }

    @BeforeEach
    fun setup() {
        given(vets.findAll()).willReturn(Lists.newArrayList(james(), helen()))
        given(vets.findAll(any<Pageable>()))
            .willReturn(PageImpl(Lists.newArrayList(james(), helen())))
    }

    @Test
    fun showVetListHtml() {
        mockMvc.perform(MockMvcRequestBuilders.get("/vets.html?page=1"))
            .andExpect(status().isOk)
            .andExpect(model().attributeExists("listVets"))
            .andExpect(view().name("vets/vetList"))
    }

    @Test
    fun showResourcesVetList() {
        val actions: ResultActions = mockMvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
        actions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.vetList[0].id").value(1))
    }

    @Test
    fun showVetListHtmlPaginated() {
        mockMvc.perform(MockMvcRequestBuilders.get("/vets.html?page=1"))
            .andExpect(status().isOk)
            .andExpect(model().attribute("currentPage", 1))
            .andExpect(model().attributeExists("totalPages"))
            .andExpect(model().attributeExists("totalItems"))
            .andExpect(model().attributeExists("listVets"))
    }

}