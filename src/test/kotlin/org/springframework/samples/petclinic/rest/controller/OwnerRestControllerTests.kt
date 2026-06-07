package org.springframework.samples.petclinic.rest.controller

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.BDDMockito.given
import org.mockito.kotlin.doAnswer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.samples.petclinic.owner.Owner
import org.springframework.samples.petclinic.owner.OwnerRepository
import org.springframework.samples.petclinic.rest.dto.OwnerCreateDto
import org.springframework.samples.petclinic.rest.dto.OwnerDto
import org.springframework.samples.petclinic.rest.dto.OwnerUpdateDto
import org.springframework.samples.petclinic.rest.mapper.OwnerMapper
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.Optional

@WebMvcTest(OwnerRestController::class)
class OwnerRestControllerTests {

    companion object {
        private const val TEST_OWNER_ID = 1
    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var ownerRepository: OwnerRepository

    @MockitoBean
    private lateinit var ownerMapper: OwnerMapper

    private fun george(): Owner {
        val george = Owner()
        george.id = TEST_OWNER_ID
        george.firstName = "George"
        george.lastName = "Franklin"
        george.address = "110 W. Liberty St."
        george.city = "Madison"
        george.telephone = "6085551023"
        george.email = "george@franklin.com"
        return george
    }

    @BeforeEach
    fun setup() {
        val george = george()
        given(ownerRepository.findAll(any<Pageable>())).willReturn(PageImpl(listOf(george)))
        given(ownerRepository.findById(TEST_OWNER_ID)).willReturn(Optional.of(george))
        given(ownerRepository.existsById(TEST_OWNER_ID)).willReturn(true)
        given(ownerRepository.save(any<Owner>())).willAnswer { invocation ->
            val owner = invocation.getArgument(0) as Owner
            if (owner.id == null) {
                owner.id = 2
            }
            owner
        }
        given(ownerMapper.toDto(any<Owner>())).willAnswer { invocation ->
            val owner = invocation.getArgument(0) as Owner
            OwnerDto(owner.id, owner.firstName, owner.lastName, owner.address, owner.city, owner.telephone, owner.email, emptyList())
        }
        given(ownerMapper.toEntity(any<OwnerCreateDto>())).willAnswer { invocation ->
            val dto = invocation.getArgument(0) as OwnerCreateDto
            val owner = Owner()
            owner.firstName = dto.firstName
            owner.lastName = dto.lastName
            owner.address = dto.address
            owner.city = dto.city
            owner.telephone = dto.telephone
            owner.email = dto.email
            owner
        }
        doAnswer { invocation ->
            val dto = invocation.getArgument(0) as OwnerUpdateDto
            val owner = invocation.getArgument(1) as Owner
            owner.firstName = dto.firstName
            owner.lastName = dto.lastName
            owner.address = dto.address
            owner.city = dto.city
            owner.telephone = dto.telephone
            owner.email = dto.email
            null
        }.`when`(ownerMapper).updateEntity(any<OwnerUpdateDto>(), any<Owner>())
    }

    @Test
    fun listOwners() {
        mockMvc.perform(get("/api/v1/owners").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").isArray)
            .andExpect(jsonPath("$.content[0].firstName").value("George"))
            .andExpect(jsonPath("$.totalElements").value(1))
    }

    @Test
    fun getOwner() {
        mockMvc.perform(get("/api/v1/owners/{ownerId}", TEST_OWNER_ID).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.firstName").value("George"))
            .andExpect(jsonPath("$.lastName").value("Franklin"))
            .andExpect(jsonPath("$.address").value("110 W. Liberty St."))
            .andExpect(jsonPath("$.city").value("Madison"))
            .andExpect(jsonPath("$.telephone").value("6085551023"))
            .andExpect(jsonPath("$.email").value("george@franklin.com"))
    }

    @Test
    fun getOwnerNotFound() {
        given(ownerRepository.findById(999)).willReturn(Optional.empty())
        mockMvc.perform(get("/api/v1/owners/999").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound)
    }

    @Test
    fun createOwner() {
        mockMvc.perform(
            post("/api/v1/owners").contentType(MediaType.APPLICATION_JSON)
                .content("""{"firstName":"Joe","lastName":"Bloggs","address":"123 Caramel Street","city":"London","telephone":"1316761638","email":"joe@bloggs.com"}""")
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.firstName").value("Joe"))
            .andExpect(jsonPath("$.email").value("joe@bloggs.com"))
    }

    @Test
    fun createOwnerWithoutEmail() {
        mockMvc.perform(
            post("/api/v1/owners").contentType(MediaType.APPLICATION_JSON)
                .content("""{"firstName":"Joe","lastName":"Bloggs","address":"123 Caramel Street","city":"London","telephone":"1316761638"}""")
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.firstName").value("Joe"))
    }

    @Test
    fun createOwnerValidationErrors() {
        mockMvc.perform(
            post("/api/v1/owners").contentType(MediaType.APPLICATION_JSON)
                .content("""{"firstName":"","lastName":"","address":"","city":"","telephone":""}""")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun updateOwner() {
        mockMvc.perform(
            put("/api/v1/owners/{ownerId}", TEST_OWNER_ID).contentType(MediaType.APPLICATION_JSON)
                .content("""{"firstName":"Joe","lastName":"Bloggs","address":"123 Caramel Street","city":"London","telephone":"1616291589"}""")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.firstName").value("Joe"))
    }

    @Test
    fun updateOwnerNotFound() {
        given(ownerRepository.findById(999)).willReturn(Optional.empty())
        mockMvc.perform(
            put("/api/v1/owners/999").contentType(MediaType.APPLICATION_JSON)
                .content("""{"firstName":"Joe","lastName":"Bloggs","address":"123 Caramel Street","city":"London","telephone":"1616291589"}""")
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun deleteOwner() {
        mockMvc.perform(delete("/api/v1/owners/{ownerId}", TEST_OWNER_ID))
            .andExpect(status().isNoContent)
    }

    @Test
    fun deleteOwnerNotFound() {
        given(ownerRepository.existsById(999)).willReturn(false)
        mockMvc.perform(delete("/api/v1/owners/999"))
            .andExpect(status().isNotFound)
    }

}