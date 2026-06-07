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
import org.springframework.samples.petclinic.owner.PetTypeRepository
import org.springframework.samples.petclinic.rest.dto.PetDto
import org.springframework.samples.petclinic.rest.dto.PetTypeDto
import org.springframework.samples.petclinic.rest.mapper.PetMapper
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate
import java.util.Optional

@WebMvcTest(PetRestController::class)
class PetRestControllerTests {

    companion object {
        private const val TEST_OWNER_ID = 1
        private const val TEST_PET_ID = 1
    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var ownerRepository: OwnerRepository

    @MockitoBean
    private lateinit var petTypeRepository: PetTypeRepository

    @MockitoBean
    private lateinit var petMapper: PetMapper

    private fun ownerWithPet(): Owner {
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
        return owner
    }

    @BeforeEach
    fun setup() {
        val dog = PetType()
        dog.id = 1
        dog.name = "dog"
        given(petTypeRepository.findById(1)).willReturn(Optional.of(dog))
        given(ownerRepository.findById(TEST_OWNER_ID)).willReturn(Optional.of(ownerWithPet()))
        given(ownerRepository.save(any<Owner>())).willAnswer { it.getArgument(0) }
        given(petMapper.toDto(any<Pet>())).willAnswer { invocation ->
            val pet = invocation.getArgument(0) as Pet
            PetDto(pet.id, pet.name, pet.birthDate, PetTypeDto(pet.type!!.id, pet.type!!.name), emptyList())
        }
    }

    @Test
    fun listPets() {
        mockMvc.perform(get("/api/v1/owners/{ownerId}/pets", TEST_OWNER_ID).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$[0].name").value("Max"))
    }

    @Test
    fun listPetsOwnerNotFound() {
        given(ownerRepository.findById(999)).willReturn(Optional.empty())
        mockMvc.perform(get("/api/v1/owners/999/pets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound)
    }

    @Test
    fun getPet() {
        mockMvc.perform(
            get("/api/v1/owners/{ownerId}/pets/{petId}", TEST_OWNER_ID, TEST_PET_ID)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Max"))
    }

    @Test
    fun getPetNotFound() {
        mockMvc.perform(
            get("/api/v1/owners/{ownerId}/pets/999", TEST_OWNER_ID).accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun createPet() {
        given(petMapper.toDto(any<Pet>())).willAnswer { invocation ->
            val pet = invocation.getArgument(0) as Pet
            PetDto(2, pet.name, pet.birthDate, PetTypeDto(1, "dog"), emptyList())
        }
        mockMvc.perform(
            post("/api/v1/owners/{ownerId}/pets", TEST_OWNER_ID).contentType(MediaType.APPLICATION_JSON)
                .content("""{"name":"Buddy","birthDate":"2023-01-01","typeId":1}""")
        )
            .andExpect(status().isCreated)
    }

    @Test
    fun createPetInvalidData() {
        mockMvc.perform(
            post("/api/v1/owners/{ownerId}/pets", TEST_OWNER_ID).contentType(MediaType.APPLICATION_JSON)
                .content("""{"name":"","birthDate":null,"typeId":null}""")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun updatePet() {
        mockMvc.perform(
            put("/api/v1/owners/{ownerId}/pets/{petId}", TEST_OWNER_ID, TEST_PET_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name":"Max Updated","birthDate":"2020-01-01","typeId":1}""")
        )
            .andExpect(status().isOk)
    }

    @Test
    fun updatePetChangesFields() {
        val newType = PetType()
        newType.id = 1
        newType.name = "dog"
        given(petTypeRepository.findById(1)).willReturn(Optional.of(newType))

        mockMvc.perform(
            put("/api/v1/owners/{ownerId}/pets/{petId}", TEST_OWNER_ID, TEST_PET_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name":"Max Updated","birthDate":"2021-06-15","typeId":1}""")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Max Updated"))
            .andExpect(jsonPath("$.birthDate").value("2021-06-15"))
    }

    @Test
    fun deletePet() {
        mockMvc.perform(delete("/api/v1/owners/{ownerId}/pets/{petId}", TEST_OWNER_ID, TEST_PET_ID))
            .andExpect(status().isNoContent)
    }

    @Test
    fun deletePetNotFound() {
        mockMvc.perform(delete("/api/v1/owners/{ownerId}/pets/999", TEST_OWNER_ID))
            .andExpect(status().isNotFound)
    }

}