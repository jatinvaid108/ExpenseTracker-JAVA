package com.example.expensetracker.controller;

import com.example.expensetracker.dto.InsightsDTO;
import com.example.expensetracker.service.FinanceAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Simple controller exposing AI insights.
 * Accepts userId as a request parameter (project does not currently use auth tokens).
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/ai")
public class FinanceAIController {

    private final FinanceAIService aiService;

    @Autowired
    private com.example.expensetracker.service.UserService userService;

    @Autowired
    public FinanceAIController(FinanceAIService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/insights")
    public ResponseEntity<?> insights(@RequestParam Long userId) {
        if (!userService.isUserActive(userId)) return ResponseEntity.status(403).body("Account deactivated");
        InsightsDTO dto = aiService.generateInsights(userId);
        return ResponseEntity.ok(dto);
    }
}
