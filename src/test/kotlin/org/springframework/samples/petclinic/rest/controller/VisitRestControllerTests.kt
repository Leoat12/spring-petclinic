package org.springframework.samples.petclinic.rest.controller

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.samples.petclinic.owner.Owner
import org.springframework.samples.petclinic.owner.OwnerRepository
import org.springframework.samples.petclinic.owner.Pet
import org.springframework.samples.petclinic.owner.PetType
import org.springframework.samples.petclinic.owner.Visit
import org.springframework.samples.petclinic.rest.dto.VisitDto
import org.springframework.samples.petclinic.rest.mapper.VisitMapper
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate
import java.util.Optional

@WebMvcTest(VisitRestController::class)
class VisitRestControllerTests {

    companion object {
        private const val TEST_OWNER_ID = 1
        private const val TEST_PET_ID = 1
    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var ownerRepository: OwnerRepository

    @MockitoBean
    private lateinit var visitMapper: VisitMapper

    private fun ownerWithPetAndVisit(): Owner {
        val owner = Owner()
        owner.id = TEST_OWNER_ID
        owner.firstName = "George"
        owner.lastName = "Franklin"
        owner.address = "110 W. Liberty St."
        owner.city = "Madison"
        owner.telephone = "6085551023"
        val dog = PetType()
        dog.id = 1
        dog.name = "dog"
        val pet = Pet()
        pet.name = "Max"
        pet.birthDate = LocalDate.of(2020, 1, 1)
        pet.type = dog
        owner.addPet(pet)
        pet.id = TEST_PET_ID
        val visit = Visit()
        visit.date = LocalDate.of(2024, 1, 15)
        visit.description = "rabies shot"
        pet.addVisit(visit)
        visit.id = 1
        return owner
    }

    @BeforeEach
    fun setup() {
        given(ownerRepository.findById(TEST_OWNER_ID)).willReturn(Optional.of(ownerWithPetAndVisit()))
        given(ownerRepository.save(any<Owner>())).willAnswer { it.getArgument(0) }
        given(visitMapper.toDto(any<Visit>())).willAnswer { invocation ->
            val visit = invocation.getArgument(0) as Visit
            VisitDto(visit.id, visit.date, visit.description)
        }
    }

    @Test
    fun listVisits() {
        mockMvc.perform(
            get("/api/v1/owners/{ownerId}/pets/{petId}/visits", TEST_OWNER_ID, TEST_PET_ID)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$[0].description").value("rabies shot"))
    }

    @Test
    fun listVisitsOwnerNotFound() {
        given(ownerRepository.findById(999)).willReturn(Optional.empty())
        mockMvc.perform(get("/api/v1/owners/999/pets/1/visits").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound)
    }

    @Test
    fun listVisitsPetNotFound() {
        mockMvc.perform(
            get("/api/v1/owners/{ownerId}/pets/999/visits", TEST_OWNER_ID).accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun createVisit() {
        given(visitMapper.toDto(any<Visit>())).willAnswer { invocation ->
            val visit = invocation.getArgument(0) as Visit
            VisitDto(2, visit.date, visit.description)
        }
        mockMvc.perform(
            post("/api/v1/owners/{ownerId}/pets/{petId}/visits", TEST_OWNER_ID, TEST_PET_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"date":"2024-03-01","description":"checkup"}""")
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.description").value("checkup"))
    }

    @Test
    fun createVisitWithNullDate() {
        given(visitMapper.toDto(any<Visit>())).willAnswer { invocation ->
            val visit = invocation.getArgument(0) as Visit
            VisitDto(2, visit.date, visit.description)
        }
        mockMvc.perform(
            post("/api/v1/owners/{ownerId}/pets/{petId}/visits", TEST_OWNER_ID, TEST_PET_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"description":"checkup no date"}""")
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.description").value("checkup no date"))
    }

    @Test
    fun createVisitBlankDescription() {
        mockMvc.perform(
            post("/api/v1/owners/{ownerId}/pets/{petId}/visits", TEST_OWNER_ID, TEST_PET_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"date":"2024-03-01","description":""}""")
        )
            .andExpect(status().isBadRequest)
    }

}