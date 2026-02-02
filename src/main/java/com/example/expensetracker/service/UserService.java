package com.example.expensetracker.service;

import com.example.expensetracker.models.User;
import com.example.expensetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public void register(User user) {
        // Hash the password
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        repository.save(user);
    }

    // Login user (compare raw password with hashed password from DB)
    public User login(String username, String rawPassword) {
        // Find user by username only
        User user = repository.findByUsername(username);

        // Check password
        if (user != null && passwordEncoder.matches(rawPassword, user.getPassword())) {
            return user;
        }

        return null; // login failed
    }


    // Check if user is active
    public boolean isUserActive(Long userId) {
        User u = repository.findById(userId);
        if (u == null) return false;
        Boolean a = u.getIsActive();
        return a == null ? true : a;
    }

}