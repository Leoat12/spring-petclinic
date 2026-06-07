package org.springframework.samples.petclinic.owner

import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.util.Assert
import org.springframework.util.StringUtils
import jakarta.validation.Valid
import org.springframework.validation.BindingResult
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.InitBinder
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.time.LocalDate

@Controller
@RequestMapping("/owners/{ownerId}")
class PetController(
    private val owners: OwnerRepository,
    private val types: PetTypeRepository
) {

    companion object {
        private const val VIEWS_PETS_CREATE_OR_UPDATE_FORM = "pets/createOrUpdatePetForm"
    }

    @ModelAttribute("types")
    fun populatePetTypes(): Collection<PetType> {
        return types.findPetTypes()
    }

    @ModelAttribute("owner")
    fun findOwner(@PathVariable("ownerId") ownerId: Int): Owner {
        return owners.findById(ownerId)
            .orElseThrow { IllegalArgumentException("Owner not found with id: $ownerId. Please ensure the ID is correct ") }
    }

    @ModelAttribute("pet")
    fun findPet(@PathVariable("ownerId") ownerId: Int, @PathVariable(name = "petId", required = false) petId: Int?): Pet {
        if (petId == null) {
            return Pet()
        }
        val owner = owners.findById(ownerId)
            .orElseThrow { IllegalArgumentException("Owner not found with id: $ownerId. Please ensure the ID is correct ") }
        return owner.getPet(petId) ?: Pet()
    }

    @InitBinder("owner")
    fun initOwnerBinder(dataBinder: WebDataBinder) {
        dataBinder.setDisallowedFields("id", "*.id")
    }

    @InitBinder("pet")
    fun initPetBinder(dataBinder: WebDataBinder) {
        dataBinder.validator = PetValidator()
        dataBinder.setDisallowedFields("id", "*.id")
    }

    @GetMapping("/pets/new")
    fun initCreationForm(owner: Owner, model: ModelMap): String {
        val pet = Pet()
        owner.addPet(pet)
        return VIEWS_PETS_CREATE_OR_UPDATE_FORM
    }

    @PostMapping("/pets/new")
    fun processCreationForm(owner: Owner, @Valid pet: Pet, result: BindingResult, redirectAttributes: RedirectAttributes): String {
        if (StringUtils.hasText(pet.name) && pet.isNew() && owner.getPet(pet.name!!, true) != null) {
            result.rejectValue("name", "duplicate", "already exists")
        }

        val currentDate = LocalDate.now()
        if (pet.birthDate != null && pet.birthDate!!.isAfter(currentDate)) {
            result.rejectValue("birthDate", "typeMismatch.birthDate")
        }

        if (result.hasErrors()) {
            return VIEWS_PETS_CREATE_OR_UPDATE_FORM
        }

        owner.addPet(pet)
        owners.save(owner)
        redirectAttributes.addFlashAttribute("message", "New Pet has been Added")
        return "redirect:/owners/{ownerId}"
    }

    @GetMapping("/pets/{petId}/edit")
    fun initUpdateForm(): String {
        return VIEWS_PETS_CREATE_OR_UPDATE_FORM
    }

    @PostMapping("/pets/{petId}/edit")
    fun processUpdateForm(owner: Owner, @Valid pet: Pet, result: BindingResult, redirectAttributes: RedirectAttributes): String {
        val petName = pet.name

        if (StringUtils.hasText(petName)) {
            val existingPet = owner.getPet(petName!!, false)
            if (existingPet != null && existingPet.id != pet.id) {
                result.rejectValue("name", "duplicate", "already exists")
            }
        }

        val currentDate = LocalDate.now()
        if (pet.birthDate != null && pet.birthDate!!.isAfter(currentDate)) {
            result.rejectValue("birthDate", "typeMismatch.birthDate")
        }

        if (result.hasErrors()) {
            return VIEWS_PETS_CREATE_OR_UPDATE_FORM
        }

        updatePetDetails(owner, pet)
        redirectAttributes.addFlashAttribute("message", "Pet details has been edited")
        return "redirect:/owners/{ownerId}"
    }

    private fun updatePetDetails(owner: Owner, pet: Pet) {
        val id = pet.id
        Assert.state(id != null, "'pet.getId()' must not be null")
        val existingPet = owner.getPet(id!!)
        if (existingPet != null) {
            existingPet.name = pet.name
            existingPet.birthDate = pet.birthDate
            existingPet.type = pet.type
        } else {
            owner.addPet(pet)
        }
        owners.save(owner)
    }

}