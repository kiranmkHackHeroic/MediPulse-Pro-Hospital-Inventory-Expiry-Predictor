package com.business.ai;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AiController {

	@Autowired
	private AiService aiService;

	@PostMapping("/chat")
	public ResponseEntity<Map<String, Object>> handleChat(@RequestBody Map<String, String> payload) {
		String userMessage = payload != null ? payload.get("message") : "";
		Map<String, Object> result = aiService.processChat(userMessage);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/insights")
	public ResponseEntity<Map<String, Object>> getAdminInsights() {
		Map<String, Object> insights = aiService.getAdminAiInsights();
		return ResponseEntity.ok(insights);
	}
}
