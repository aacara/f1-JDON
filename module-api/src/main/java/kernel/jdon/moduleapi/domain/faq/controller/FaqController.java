package kernel.jdon.moduleapi.domain.faq.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import kernel.jdon.moduleapi.domain.faq.dto.FindFaqResponse;
import kernel.jdon.moduleapi.domain.faq.service.FaqService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FaqController {
	private final FaqService faqService;

	@GetMapping("/api/v1/faqs/{faqId}")
	public ResponseEntity<FindFaqResponse> get(@PathVariable(name = "faqId") Long faqId) {
		FindFaqResponse findFaqResponse = faqService.find(faqId);

		return ResponseEntity.ok().body(findFaqResponse);
	}

	@DeleteMapping("/api/v1/faqs/{faqId}")
	public ResponseEntity<Long> remove(@PathVariable(name = "faqId") Long faqId) {
		faqService.delete(faqId);

		return ResponseEntity.ok(faqId);
	}

}