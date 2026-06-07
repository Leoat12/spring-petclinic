package org.springframework.samples.petclinic.owner

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledInNativeImage
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.test.context.aot.DisabledInAotMode
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.model
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.view
import java.util.Optional

@WebMvcTest(VisitController::class)
@DisabledInNativeImage
@DisabledInAotMode
class VisitControllerTests {

    companion object {
        private const val TEST_OWNER_ID = 1
        private const val TEST_PET_ID = 1
    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var owners: OwnerRepository

    @BeforeEach
    fun init() {
        val owner = Owner()
        val pet = Pet()
        owner.addPet(pet)
        pet.id = TEST_PET_ID
        given(owners.findById(TEST_OWNER_ID)).willReturn(Optional.of(owner))
    }

    @Test
    fun initNewVisitForm() {
        mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID))
            .andExpect(status().isOk)
            .andExpect(view().name("pets/createOrUpdateVisitForm"))
    }

    @Test
    fun processNewVisitFormSuccess() {
        mockMvc.perform(
            post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
                .param("name", "George")
                .param("description", "Visit Description")
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(view().name("redirect:/owners/{ownerId}"))
    }

    @Test
    fun processNewVisitFormHasErrors() {
        mockMvc.perform(
            post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
                .param("name", "George")
        )
            .andExpect(model().attributeHasErrors("visit"))
            .andExpect(status().isOk)
            .andExpect(view().name("pets/createOrUpdateVisitForm"))
    }

    @Test
    fun shouldAddVisitToPetInModelAttribute() {
        mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID))
            .andExpect(status().isOk)
            .andExpect(model().attributeExists("visit"))
            .andExpect(model().attributeExists("pet"))
            .andExpect(model().attributeExists("owner"))
    }

}