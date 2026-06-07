package org.springframework.samples.petclinic.owner

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace
import org.springframework.context.annotation.Import
import org.springframework.data.domain.Pageable
import org.springframework.samples.petclinic.service.EntityUtils
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@DataJdbcTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(JdbcClientOwnerRepository::class)
class OwnerRepositoryTests {

    @Autowired
    protected lateinit var owners: OwnerRepository

    private val pageable = Pageable.unpaged()

    @Test
    fun shouldFindOwnersByLastName() {
        var results = owners.findByLastNameStartingWith("Davis", pageable)
        assertThat(results).hasSize(2)

        results = owners.findByLastNameStartingWith("Daviss", pageable)
        assertThat(results).isEmpty()
    }

    @Test
    fun shouldFindOwnersByLastNamePartialMatch() {
        val results = owners.findByLastNameStartingWith("Da", pageable)
        assertThat(results.totalElements).isGreaterThanOrEqualTo(2)
    }

    @Test
    fun shouldFindAllOwners() {
        val results = owners.findAll(pageable)
        assertThat(results.totalElements).isGreaterThan(0)
    }

    @Test
    fun shouldFindSingleOwnerWithPet() {
        val optionalOwner = owners.findById(1)
        assertThat(optionalOwner).isPresent
        val owner = optionalOwner.get()
        assertThat(owner.lastName).startsWith("Franklin")
        assertThat(owner.getPets()).hasSize(1)
        assertThat(owner.getPets()[0].type).isNotNull()
        assertThat(owner.getPets()[0].type!!.name).isEqualTo("cat")
    }

    @Test
    fun shouldFindOwnerById() {
        val optionalOwner = owners.findById(1)
        assertThat(optionalOwner).isPresent
        assertThat(optionalOwner.get().firstName).isEqualTo("George")
    }

    @Test
    fun shouldReturnEmptyForNonExistentOwner() {
        val optionalOwner = owners.findById(999)
        assertThat(optionalOwner).isEmpty
    }

    @Test
    @Transactional
    fun shouldInsertOwner() {
        var ownersPage = owners.findByLastNameStartingWith("Schultz", pageable)
        val found = ownersPage.totalElements.toInt()

        val owner = Owner()
        owner.firstName = "Sam"
        owner.lastName = "Schultz"
        owner.address = "4, Evans Street"
        owner.city = "Wollongong"
        owner.telephone = "4444444444"
        owner.email = "sam@schultz.com"
        owners.save(owner)
        assertThat(owner.id).isNotZero()

        ownersPage = owners.findByLastNameStartingWith("Schultz", pageable)
        assertThat(ownersPage.totalElements).isEqualTo((found + 1).toLong())
    }

    @Test
    @Transactional
    fun shouldInsertOwnerWithoutEmail() {
        val owner = Owner()
        owner.firstName = "NoEmail"
        owner.lastName = "Owner"
        owner.address = "123 Street"
        owner.city = "City"
        owner.telephone = "1234567890"
        owners.save(owner)
        assertThat(owner.id).isNotZero()

        val loaded = owners.findById(owner.id!!)
        assertThat(loaded).isPresent
        assertThat(loaded.get().email).isNull()
    }

    @Test
    @Transactional
    fun shouldPersistEmail() {
        val owner = Owner()
        owner.firstName = "Email"
        owner.lastName = "Test"
        owner.address = "123 Street"
        owner.city = "City"
        owner.telephone = "1234567890"
        owner.email = "email@test.com"
        owners.save(owner)

        val loaded = owners.findById(owner.id!!)
        assertThat(loaded).isPresent
        assertThat(loaded.get().email).isEqualTo("email@test.com")
    }

    @Test
    @Transactional
    fun shouldUpdateEmail() {
        var optionalOwner = owners.findById(1)
        assertThat(optionalOwner).isPresent
        val owner = optionalOwner.get()
        owner.email = "updated@email.com"
        owners.save(owner)

        val reloaded = owners.findById(1)
        assertThat(reloaded).isPresent
        assertThat(reloaded.get().email).isEqualTo("updated@email.com")
    }

    @Test
    @Transactional
    fun shouldClearEmail() {
        var optionalOwner = owners.findById(1)
        assertThat(optionalOwner).isPresent
        val owner = optionalOwner.get()
        owner.email = null
        owners.save(owner)

        val reloaded = owners.findById(1)
        assertThat(reloaded).isPresent
        assertThat(reloaded.get().email).isNull()
    }

    @Test
    @Transactional
    fun shouldUpdateOwner() {
        var optionalOwner = owners.findById(1)
        assertThat(optionalOwner).isPresent
        val owner = optionalOwner.get()
        val oldLastName = owner.lastName
        val newLastName = oldLastName + "X"

        owner.lastName = newLastName
        owners.save(owner)

        optionalOwner = owners.findById(1)
        assertThat(optionalOwner).isPresent
        assertThat(optionalOwner.get().lastName).isEqualTo(newLastName)
    }

    @Test
    @Transactional
    fun shouldDeleteOwner() {
        val owner = Owner()
        owner.firstName = "Delete"
        owner.lastName = "Me"
        owner.address = "123 Street"
        owner.city = "City"
        owner.telephone = "1234567890"
        owners.save(owner)
        val id = owner.id!!
        assertThat(owners.existsById(id)).isTrue()

        owners.deleteById(id)
        assertThat(owners.existsById(id)).isFalse()
    }

    @Test
    fun shouldCheckOwnerExists() {
        assertThat(owners.existsById(1)).isTrue()
        assertThat(owners.existsById(999)).isFalse()
    }

    @Test
    @Transactional
    fun shouldInsertPetIntoOwner() {
        var optionalOwner = owners.findById(6)
        assertThat(optionalOwner).isPresent
        var owner6 = optionalOwner.get()

        val found = owner6.getPets().size

        val pet = Pet()
        pet.name = "bowser"
        val types = owners.findAll(pageable)
            .content
            .stream()
            .flatMap { o: Owner -> o.getPets().stream() }
            .map { p: Pet -> p.type!! }
            .distinct()
            .toList()
        val petType = EntityUtils.getById(types, PetType::class.java, 2)
        pet.type = petType
        pet.birthDate = LocalDate.now()
        owner6.addPet(pet)
        assertThat(owner6.getPets()).hasSize(found + 1)

        owners.save(owner6)

        optionalOwner = owners.findById(6)
        assertThat(optionalOwner).isPresent
        owner6 = optionalOwner.get()
        assertThat(owner6.getPets()).hasSize(found + 1)
        val saved = owner6.getPet("bowser")
        assertThat(saved!!.id).isNotNull()
    }

    @Test
    @Transactional
    fun shouldAddNewVisitForPet() {
        var optionalOwner = owners.findById(6)
        assertThat(optionalOwner).isPresent
        val owner6 = optionalOwner.get()

        val pet7 = owner6.getPet(7)!!
        val found = pet7.getVisits().size
        val visit = Visit()
        visit.description = "test"

        owner6.addVisit(pet7.id!!, visit)
        owners.save(owner6)

        assertThat(pet7.getVisits()).hasSize(found + 1).allMatch { v: Visit -> v.id != null }
    }

    @Test
    fun shouldLoadOwnerWithPetAndVisitDetails() {
        val optionalOwner = owners.findById(6)
        assertThat(optionalOwner).isPresent
        val owner = optionalOwner.get()

        val pets = owner.getPets()
        assertThat(pets).isNotEmpty()

        val pet = pets[0]
        assertThat(pet.id).isNotNull()
        assertThat(pet.name).isNotNull()
        assertThat(pet.birthDate).isNotNull()
        assertThat(pet.type).isNotNull()
        assertThat(pet.type!!.id).isNotNull()
        assertThat(pet.type!!.name).isNotNull()
    }

    @Test
    fun shouldLoadPetDetailsForOwnersViaFindByLastName() {
        val results = owners.findByLastNameStartingWith("Coleman", pageable)
        assertThat(results.content).hasSize(1)
        val owner = results.content[0]
        assertThat(owner.getPets()).isNotEmpty()
        for (pet in owner.getPets()) {
            assertThat(pet.id).isNotNull()
            assertThat(pet.name).isNotNull()
            assertThat(pet.birthDate).isNotNull()
            assertThat(pet.type).isNotNull()
            assertThat(pet.type!!.id).isNotNull()
        }
    }

    @Test
    fun shouldLoadVisitDetailsForOwner() {
        val optionalOwner = owners.findById(6)
        assertThat(optionalOwner).isPresent
        val owner = optionalOwner.get()

        val pet7 = owner.getPet(7)
        assertThat(pet7).isNotNull()
        assertThat(pet7!!.getVisits()).isNotEmpty()
        val visit = pet7.getVisits().iterator().next()
        assertThat(visit.id).isNotNull()
        assertThat(visit.date).isNotNull()
        assertThat(visit.description).isNotNull()
    }

    @Test
    fun shouldLoadPetTypesForOwnersViaFindByLastName() {
        val results = owners.findByLastNameStartingWith("", pageable)
        assertThat(results.content).isNotEmpty()
        for (owner in results.content) {
            for (pet in owner.getPets()) {
                assertThat(pet.type).isNotNull()
                assertThat(pet.type!!.name).isNotNull()
                assertThat(pet.name).isNotNull()
                assertThat(pet.birthDate).isNotNull()
            }
        }
    }

    @Test
    fun shouldLoadVisitDetailsForOwnersViaFindByLastName() {
        val results = owners.findByLastNameStartingWith("", pageable)
        assertThat(results.content).isNotEmpty()
        var hasVisits = false
        for (owner in results.content) {
            for (pet in owner.getPets()) {
                assertThat(pet.id).isNotNull()
                for (visit in pet.getVisits()) {
                    assertThat(visit.id).isNotNull()
                    assertThat(visit.date).isNotNull()
                    assertThat(visit.description).isNotNull()
                    hasVisits = true
                }
            }
        }
        assertThat(hasVisits).isTrue()
    }

    @Test
    fun shouldLoadVisitDetailsForOwnerViaFindById() {
        val optionalOwner = owners.findById(6)
        assertThat(optionalOwner).isPresent
        val owner = optionalOwner.get()
        val pet7 = owner.getPet(7)
        assertThat(pet7).isNotNull()
        var foundVisitWithDetails = false
        for (visit in pet7!!.getVisits()) {
            assertThat(visit.id).isNotNull()
            assertThat(visit.date).isNotNull()
            assertThat(visit.description).isNotNull()
            foundVisitWithDetails = true
        }
        assertThat(foundVisitWithDetails).isTrue()
    }

    @Test
    @Transactional
    fun shouldDeleteOwnerCascadesVisitsAndPets() {
        val owner = Owner()
        owner.firstName = "Cascade"
        owner.lastName = "Test"
        owner.address = "123 St"
        owner.city = "City"
        owner.telephone = "1234567890"
        owners.save(owner)
        val id = owner.id!!

        val types = owners.findAll(pageable)
            .content
            .stream()
            .flatMap { o: Owner -> o.getPets().stream() }
            .map { p: Pet -> p.type!! }
            .distinct()
            .toList()
        val petType = EntityUtils.getById(types, PetType::class.java, 1)

        val pet = Pet()
        pet.name = "cascade_pet"
        pet.birthDate = LocalDate.now()
        pet.type = petType
        owner.addPet(pet)
        owners.save(owner)

        val visit = Visit()
        visit.description = "cascade_visit"
        owner.addVisit(pet.id!!, visit)
        owners.save(owner)

        owners.deleteById(id)
        assertThat(owners.existsById(id)).isFalse()
    }

    @Test
    @Transactional
    fun shouldSaveNewPetWithTypeAndIdSet() {
        var optionalOwner = owners.findById(6)
        assertThat(optionalOwner).isPresent
        val owner = optionalOwner.get()

        val types = owners.findAll(pageable)
            .content
            .stream()
            .flatMap { o: Owner -> o.getPets().stream() }
            .map { p: Pet -> p.type!! }
            .distinct()
            .toList()
        val petType = EntityUtils.getById(types, PetType::class.java, 2)

        val newPet = Pet()
        newPet.name = "new_test_pet"
        newPet.birthDate = LocalDate.now()
        newPet.type = petType
        owner.addPet(newPet)
        owners.save(owner)

        val reloaded = owners.findById(6)
        assertThat(reloaded).isPresent
        val savedPet = reloaded.get().getPet("new_test_pet")
        assertThat(savedPet).isNotNull()
        assertThat(savedPet!!.id).isNotNull()
        assertThat(savedPet.typeId).isNotNull()
    }

    @Test
    @Transactional
    fun shouldUpdateExistingPetFields() {
        var optionalOwner = owners.findById(6)
        assertThat(optionalOwner).isPresent
        val owner = optionalOwner.get()

        val pet7 = owner.getPet(7)!!
        assertThat(pet7).isNotNull
        val originalName = pet7.name

        val types = owners.findAll(pageable)
            .content
            .stream()
            .flatMap { o: Owner -> o.getPets().stream() }
            .map { p: Pet -> p.type!! }
            .distinct()
            .toList()
        val newType = EntityUtils.getById(types, PetType::class.java, 1)
        pet7.name = "$originalName _updated"
        pet7.type = newType
        owners.save(owner)

        val reloaded = owners.findById(6)
        assertThat(reloaded).isPresent
        val updatedPet = reloaded.get().getPet("$originalName _updated")
        assertThat(updatedPet).isNotNull()
        assertThat(updatedPet!!.typeId).isEqualTo(newType.id)
    }

    @Test
    @Transactional
    fun shouldSaveVisitWithDateAndDescription() {
        var optionalOwner = owners.findById(6)
        assertThat(optionalOwner).isPresent
        val owner = optionalOwner.get()

        val pet7 = owner.getPet(7)!!
        val visitDate = LocalDate.of(2024, 6, 15)
        val visit = Visit()
        visit.date = visitDate
        visit.description = "annual checkup"
        owner.addVisit(pet7.id!!, visit)
        owners.save(owner)

        val reloaded = owners.findById(6)
        assertThat(reloaded).isPresent
        val reloadedPet = reloaded.get().getPet(7)!!
        var found = false
        for (v in reloadedPet.getVisits()) {
            if (v.description == "annual checkup") {
                assertThat(v.date).isEqualTo(visitDate)
                assertThat(v.id).isNotNull()
                found = true
            }
        }
        assertThat(found).isTrue()
    }

    @Test
    @Transactional
    fun shouldRemovePetFromOwner() {
        var optionalOwner = owners.findById(6)
        assertThat(optionalOwner).isPresent
        val owner = optionalOwner.get()
        val initialPetCount = owner.getPets().size
        assertThat(initialPetCount).isGreaterThan(0)

        val petToRemove = owner.getPets()[0]
        owner.getPets().remove(petToRemove)
        owners.save(owner)

        val reloaded = owners.findById(6)
        assertThat(reloaded).isPresent()
        assertThat(reloaded.get().getPets()).hasSize(initialPetCount - 1)
    }

    @Test
    fun shouldLoadAllOwnersWithPetDetails() {
        val allOwners = owners.findAll()
        assertThat(allOwners).isNotEmpty()
        for (owner in allOwners) {
            for (pet in owner.getPets()) {
                assertThat(pet.id).isNotNull()
                assertThat(pet.name).isNotNull()
                assertThat(pet.birthDate).isNotNull()
                assertThat(pet.type).isNotNull()
                assertThat(pet.type!!.name).isNotNull()
            }
        }
    }

    @Test
    fun shouldLoadAllOwnersWithVisitDetails() {
        val allOwners = owners.findAll()
        assertThat(allOwners).isNotEmpty()
        var hasVisits = false
        for (owner in allOwners) {
            for (pet in owner.getPets()) {
                for (visit in pet.getVisits()) {
                    assertThat(visit.id).isNotNull()
                    assertThat(visit.date).isNotNull()
                    assertThat(visit.description).isNotNull()
                    hasVisits = true
                }
            }
        }
        assertThat(hasVisits).isTrue()
    }

}