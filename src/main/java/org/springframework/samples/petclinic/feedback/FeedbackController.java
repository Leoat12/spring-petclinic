package org.springframework.samples.petclinic.feedback;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
class FeedbackController {

	private static final String VIEWS_FEEDBACK_FORM = "feedback/feedbackForm";

	private final FeedbackRepository feedbackRepository;

	public FeedbackController(FeedbackRepository feedbackRepository) {
		this.feedbackRepository = feedbackRepository;
	}

	@GetMapping("/feedback/new")
	public String initCreationForm(Model model) {
		model.addAttribute("feedback", new Feedback());
		return VIEWS_FEEDBACK_FORM;
	}

	@PostMapping("/feedback/new")
	public String processCreationForm(@Valid Feedback feedback, BindingResult result,
			RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			return VIEWS_FEEDBACK_FORM;
		}
		feedbackRepository.save(feedback);
		redirectAttributes.addFlashAttribute("message", "Thank you for your feedback!");
		return "redirect:/feedback/new";
	}

	@GetMapping("/admin/feedback")
	public String showFeedbackList(Model model) {
		model.addAttribute("feedbackList", feedbackRepository.findAll());
		return "feedback/feedbackList";
	}

}