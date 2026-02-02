package com.example.expensetracker.service;

import com.example.expensetracker.models.Expense;
import com.example.expensetracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository repository;

    @Autowired
    public ExpenseService(ExpenseRepository repository) {
        this.repository = repository;
    }

    // CREATE
    public void addExpense(Long userId, Expense expense) {
        if (expense.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be positive!");
        }
        repository.addExpense(userId, expense);
    }

    // READ
    public List<Expense> getExpenses(Long userId) {
        return repository.getAllExpenses(userId);
    }

        // SEARCH
    public List<Expense> searchExpenses(Long userId, String keyword) {
        return repository.searchExpenses(userId, keyword);
    }

    // FILTER
    public List<Expense> filterExpenses(Long userId, String category) {
        return repository.filterExpenses(userId, category);
    }

    // SEARCH + FILTER
    public List<Expense> searchAndFilter(Long userId, String category, String keyword) {
        return repository.searchAndFilter(userId, category, keyword);
    }

    // UPDATE
    public void updateExpense(Long userId, Long id, Expense expense) {
        if (expense.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be positive!");
        }
        int rows = repository.updateExpense(userId, id, expense);
        if (rows == 0) {
            throw new RuntimeException("Expense not found with id " + id);
        }
    }

    // DELETE
    public void deleteExpense(Long userId, Long id) {
        int rows = repository.deleteExpense(userId, id);
        if (rows == 0) {
            throw new RuntimeException("Expense not found with id " + id);
        }
    }
}
