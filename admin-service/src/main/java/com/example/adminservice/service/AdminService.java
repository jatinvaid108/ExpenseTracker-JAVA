package com.example.adminservice.service;

import com.example.adminservice.models.Expense;
import com.example.adminservice.models.User;
import com.example.adminservice.repository.ExpenseRepository;
import com.example.adminservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    public List<User> allUsers() { return userRepository.findAll(); }

    public User getUser(Long id) { return userRepository.findById(id); }

    public List<Expense> getUserExpenses(Long userId) { return expenseRepository.findByUserId(userId); }

    public int updateUser(Long id, String email, Boolean isActive) { return userRepository.updateUser(id, email, isActive); }

    public int deleteUser(Long id) { return userRepository.deleteUser(id); }

    public int setActive(Long id, boolean active) { return userRepository.setActive(id, active); }

    public int updateExpense(Long id, Expense e) { return expenseRepository.updateExpense(id, e); }

    public int deleteExpense(Long id) { return expenseRepository.deleteExpense(id); }
}
