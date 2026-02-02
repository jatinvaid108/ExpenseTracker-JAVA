package com.example.expensetracker.controller;

import com.example.expensetracker.models.Expense;
import com.example.expensetracker.service.ExpenseService;
import com.example.expensetracker.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService service;

    @Autowired
    private UserService userService;

     private ResponseEntity<String> checkActive(Long userId) {
        if (!userService.isUserActive(userId)) {
            return ResponseEntity
                    .status(403)
                    .body("Your account has been deactivated. Please contact admin.");
        }
        return null;
    }
    // CREATE
    @PostMapping
    public ResponseEntity<String> addExpense(@RequestBody Expense expense,
                             @RequestParam Long userId) {
        ResponseEntity<String> r = checkActive(userId);
        if (r != null) return r;
        service.addExpense(userId, expense);
        return ResponseEntity.ok("Expense added successfully!");
    }

    // READ
    @GetMapping
    public List<Expense> getAllExpenses(@RequestParam Long userId) {
        return service.getExpenses(userId);
    }

        // SEARCH
    @GetMapping("/search")
    public List<Expense> searchExpenses(@RequestParam Long userId,
                                        @RequestParam String keyword) {
        return service.searchExpenses(userId, keyword);
    }

    // FILTER
    @GetMapping("/filter")
    public List<Expense> filterExpenses(@RequestParam Long userId,
                                        @RequestParam String category) {
        return service.filterExpenses(userId, category);
    }

    // SEARCH + FILTER
    @GetMapping("/search-filter")
    public List<Expense> searchAndFilter(@RequestParam Long userId,
                                         @RequestParam String category,
                                         @RequestParam String keyword) {
        return service.searchAndFilter(userId, category, keyword);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<String> updateExpense(@PathVariable Long id,
                                                @RequestBody Expense expense,
                                                @RequestParam Long userId) {
        ResponseEntity<String> r = checkActive(userId);
        if (r != null) return r;
        service.updateExpense(userId, id, expense);
        return ResponseEntity.ok("Expense updated successfully!");
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExpense(@PathVariable Long id,
                                                @RequestParam Long userId) {
        ResponseEntity<String> r = checkActive(userId);
        if (r != null) return r;
        service.deleteExpense(id, userId);
        return ResponseEntity.ok("Expense deleted successfully!");
    }
}