package org.springframework.samples.petclinic.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace
import org.springframework.context.annotation.Import
import org.springframework.data.domain.Pageable
import org.springframework.samples.petclinic.owner.JdbcClientOwnerRepository
import org.springframework.samples.petclinic.owner.JdbcClientPetTypeRepository
import org.springframework.samples.petclinic.owner.Owner
import org.springframework.samples.petclinic.owner.OwnerRepository
import org.springframework.samples.petclinic.owner.Pet
import org.springframework.samples.petclinic.owner.PetType
import org.springframework.samples.petclinic.owner.PetTypeRepository
import org.springframework.samples.petclinic.owner.Visit
import org.springframework.samples.petclinic.vet.JdbcClientVetRepository
import org.springframework.samples.petclinic.vet.Vet
import org.springframework.samples.petclinic.vet.VetRepository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@DataJdbcTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(JdbcClientOwnerRepository::class, JdbcClientPetTypeRepository::class, JdbcClientVetRepository::class)
class ClinicServiceTests {

    @Autowired
    protected lateinit var owners: OwnerRepository

    @Autowired
    protected lateinit var types: PetTypeRepository

    @Autowired
    protected lateinit var vets: VetRepository

    private val pageable = Pageable.unpaged()

    @Test
    fun shouldFindOwnersByLastName() {
        var owners = this.owners.findByLastNameStartingWith("Davis", pageable)
        assertThat(owners).hasSize(2)

        owners = this.owners.findByLastNameStartingWith("Daviss", pageable)
        assertThat(owners).isEmpty()
    }

    @Test
    fun shouldFindSingleOwnerWithPet() {
        val optionalOwner = this.owners.findById(1)
        assertThat(optionalOwner).isPresent
        val owner = optionalOwner.get()
        assertThat(owner.lastName).startsWith("Franklin")
        assertThat(owner.getPets()).hasSize(1)
        assertThat(owner.getPets()[0].type).isNotNull()
        assertThat(owner.getPets()[0].type!!.name).isEqualTo("cat")
    }

    @Test
    @Transactional
    fun shouldInsertOwner() {
        var owners = this.owners.findByLastNameStartingWith("Schultz", pageable)
        val found = owners.totalElements.toInt()

        val owner = Owner()
        owner.firstName = "Sam"
        owner.lastName = "Schultz"
        owner.address = "4, Evans Street"
        owner.city = "Wollongong"
        owner.telephone = "4444444444"
        this.owners.save(owner)
        assertThat(owner.id).isNotZero()

        owners = this.owners.findByLastNameStartingWith("Schultz", pageable)
        assertThat(owners.totalElements).isEqualTo((found + 1).toLong())
    }

    @Test
    @Transactional
    fun shouldUpdateOwner() {
        var optionalOwner = this.owners.findById(1)
        assertThat(optionalOwner).isPresent
        val owner = optionalOwner.get()
        val oldLastName = owner.lastName
        val newLastName = oldLastName + "X"

        owner.lastName = newLastName
        this.owners.save(owner)

        optionalOwner = this.owners.findById(1)
        assertThat(optionalOwner).isPresent
        val updatedOwner = optionalOwner.get()
        assertThat(updatedOwner.lastName).isEqualTo(newLastName)
    }

    @Test
    fun shouldFindAllPetTypes() {
        val petTypes = this.types.findPetTypes()

        val petType1 = EntityUtils.getById(petTypes, PetType::class.java, 1)
        assertThat(petType1.name).isEqualTo("cat")
        val petType4 = EntityUtils.getById(petTypes, PetType::class.java, 4)
        assertThat(petType4.name).isEqualTo("snake")
    }

    @Test
    @Transactional
    fun shouldInsertPetIntoDatabaseAndGenerateId() {
        var optionalOwner = this.owners.findById(6)
        assertThat(optionalOwner).isPresent
        var owner6 = optionalOwner.get()

        val found = owner6.getPets().size

        val pet = Pet()
        pet.name = "bowser"
        val types = this.types.findPetTypes()
        pet.type = EntityUtils.getById(types, PetType::class.java, 2)
        pet.birthDate = LocalDate.now()
        owner6.addPet(pet)
        assertThat(owner6.getPets()).hasSize(found + 1)

        this.owners.save(owner6)

        optionalOwner = this.owners.findById(6)
        assertThat(optionalOwner).isPresent
        owner6 = optionalOwner.get()
        assertThat(owner6.getPets()).hasSize(found + 1)
        var savedPet = owner6.getPet("bowser")
        assertThat(savedPet!!.id).isNotNull()
    }

    @Test
    @Transactional
    fun shouldUpdatePetName() {
        var optionalOwner = this.owners.findById(6)
        assertThat(optionalOwner).isPresent
        var owner6 = optionalOwner.get()

        val pet7 = owner6.getPet(7)!!
        val oldName = pet7.name

        val newName = oldName + "X"
        pet7.name = newName
        this.owners.save(owner6)

        optionalOwner = this.owners.findById(6)
        assertThat(optionalOwner).isPresent
        owner6 = optionalOwner.get()
        val updatedPet = owner6.getPet(7)!!
        assertThat(updatedPet.name).isEqualTo(newName)
    }

    @Test
    fun shouldFindVets() {
        val vets = this.vets.findAll()

        val vet = EntityUtils.getById(vets, Vet::class.java, 3)
        assertThat(vet.lastName).isEqualTo("Douglas")
        assertThat(vet.getNrOfSpecialties()).isEqualTo(2)
        assertThat(vet.getSpecialties()[0].name).isEqualTo("dentistry")
        assertThat(vet.getSpecialties()[1].name).isEqualTo("surgery")
    }

    @Test
    @Transactional
    fun shouldAddNewVisitForPet() {
        var optionalOwner = this.owners.findById(6)
        assertThat(optionalOwner).isPresent
        val owner6 = optionalOwner.get()

        val pet7 = owner6.getPet(7)!!
        val found = pet7.getVisits().size
        val visit = Visit()
        visit.description = "test"

        owner6.addVisit(pet7.id!!, visit)
        this.owners.save(owner6)

        assertThat(pet7.getVisits())
            .hasSize(found + 1)
            .allMatch { v: Visit -> v.id != null }
    }

    @Test
    fun shouldFindVisitsByPetId() {
        val optionalOwner = this.owners.findById(6)
        assertThat(optionalOwner).isPresent
        val owner6 = optionalOwner.get()

        val pet7 = owner6.getPet(7)!!
        val visits = pet7.getVisits()

        assertThat(visits)
            .hasSize(2)
            .element(0)
            .extracting(Visit::date)
            .isNotNull()
    }

}