package org.springframework.samples.petclinic.owner

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Controller
import jakarta.validation.Valid
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.InitBinder
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class OwnerController(
    private val owners: OwnerRepository
) {

    companion object {
        private const val VIEWS_OWNER_CREATE_OR_UPDATE_FORM = "owners/createOrUpdateOwnerForm"
    }

    @InitBinder
    fun setAllowedFields(dataBinder: WebDataBinder) {
        dataBinder.setDisallowedFields("id", "*.id")
    }

    @ModelAttribute("owner")
    fun findOwner(@PathVariable(name = "ownerId", required = false) ownerId: Int?): Owner {
        return ownerId?.let {
            owners.findById(it)
                .orElseThrow { IllegalArgumentException("Owner not found with id: $it. Please ensure the ID is correct and the owner exists in the database.") }
        } ?: Owner()
    }

    @GetMapping("/owners/new")
    fun initCreationForm(): String {
        return VIEWS_OWNER_CREATE_OR_UPDATE_FORM
    }

    @PostMapping("/owners/new")
    fun processCreationForm(@Valid owner: Owner, result: BindingResult, redirectAttributes: RedirectAttributes): String {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "There was an error in creating the owner.")
            return VIEWS_OWNER_CREATE_OR_UPDATE_FORM
        }

        owners.save(owner)
        redirectAttributes.addFlashAttribute("message", "New Owner Created")
        return "redirect:/owners/${owner.id}"
    }

    @GetMapping("/owners/find")
    fun initFindForm(): String {
        return "owners/findOwners"
    }

    @GetMapping("/owners")
    fun processFindForm(@RequestParam(defaultValue = "1") page: Int, owner: Owner, result: BindingResult, model: Model): String {
        var lastName = owner.lastName
        if (lastName == null) {
            lastName = ""
        }

        val ownersResults: Page<Owner> = findPaginatedForOwnersLastName(page, lastName)
        if (ownersResults.isEmpty) {
            result.rejectValue("lastName", "notFound", "not found")
            return "owners/findOwners"
        }

        if (ownersResults.totalElements == 1L) {
            val foundOwner = ownersResults.iterator().next()
            return "redirect:/owners/${foundOwner.id}"
        }

        return addPaginationModel(page, model, ownersResults)
    }

    private fun addPaginationModel(page: Int, model: Model, paginated: Page<Owner>): String {
        val listOwners = paginated.content
        model.addAttribute("currentPage", page)
        model.addAttribute("totalPages", paginated.totalPages)
        model.addAttribute("totalItems", paginated.totalElements)
        model.addAttribute("listOwners", listOwners)
        return "owners/ownersList"
    }

    private fun findPaginatedForOwnersLastName(page: Int, lastname: String): Page<Owner> {
        val pageSize = 5
        val pageable: Pageable = PageRequest.of(page - 1, pageSize)
        return owners.findByLastNameStartingWith(lastname, pageable)
    }

    @GetMapping("/owners/{ownerId}/edit")
    fun initUpdateOwnerForm(): String {
        return VIEWS_OWNER_CREATE_OR_UPDATE_FORM
    }

    @PostMapping("/owners/{ownerId}/edit")
    fun processUpdateOwnerForm(
        @Valid owner: Owner,
        result: BindingResult,
        @PathVariable("ownerId") ownerId: Int,
        redirectAttributes: RedirectAttributes
    ): String {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "There was an error in updating the owner.")
            return VIEWS_OWNER_CREATE_OR_UPDATE_FORM
        }

        if (owner.id != ownerId) {
            result.rejectValue("id", "mismatch", "The owner ID in the form does not match the URL.")
            redirectAttributes.addFlashAttribute("error", "Owner ID mismatch. Please try again.")
            return "redirect:/owners/{ownerId}/edit"
        }

        owner.id = ownerId
        owners.save(owner)
        redirectAttributes.addFlashAttribute("message", "Owner Values Updated")
        return "redirect:/owners/{ownerId}"
    }

    @GetMapping("/owners/{ownerId}")
    fun showOwner(@PathVariable("ownerId") ownerId: Int): ModelAndView {
        val mav = ModelAndView("owners/ownerDetails")
        val owner = owners.findById(ownerId)
            .orElseThrow { IllegalArgumentException("Owner not found with id: $ownerId. Please ensure the ID is correct ") }
        mav.addObject(owner)
        return mav
    }

}