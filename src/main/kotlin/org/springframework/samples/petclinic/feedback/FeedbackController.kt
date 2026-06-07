package org.springframework.samples.petclinic.feedback

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import jakarta.validation.Valid
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class FeedbackController(
    private val feedbackRepository: FeedbackRepository
) {

    companion object {
        private const val VIEWS_FEEDBACK_FORM = "feedback/feedbackForm"
    }

    @GetMapping("/feedback/new")
    fun initCreationForm(model: Model): String {
        model.addAttribute("feedback", Feedback())
        return VIEWS_FEEDBACK_FORM
    }

    @PostMapping("/feedback/new")
    fun processCreationForm(@Valid feedback: Feedback, result: BindingResult, redirectAttributes: RedirectAttributes): String {
        if (result.hasErrors()) {
            return VIEWS_FEEDBACK_FORM
        }
        feedbackRepository.save(feedback)
        redirectAttributes.addFlashAttribute("message", "Thank you for your feedback!")
        return "redirect:/feedback/new"
    }

    @GetMapping("/admin/feedback")
    fun showFeedbackList(model: Model): String {
        model.addAttribute("feedbackList", feedbackRepository.findAll())
        return "feedback/feedbackList"
    }

}