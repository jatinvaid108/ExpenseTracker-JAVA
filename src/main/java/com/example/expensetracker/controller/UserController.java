package com.example.expensetracker.controller;

import com.example.expensetracker.models.User;
import com.example.expensetracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // SIGNUP
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody User user) {
        userService.register(user);
        return ResponseEntity.ok("User registered successfully!");
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) {
        User loggedInUser = userService.login(user.getUsername(), user.getPassword());
        if (loggedInUser != null) {
            // if user is deactivated, return 403
            Boolean a = loggedInUser.getIsActive();
            if (a != null && !a) {
                return ResponseEntity.status(403).build();
            }
            return ResponseEntity.ok(loggedInUser);
        } else {
            return ResponseEntity.status(401).build();
        }
    }

    @GetMapping("/{id}/status")
public ResponseEntity<?> checkStatus(@PathVariable Long id) {
    if (!userService.isUserActive(id)) {
        return ResponseEntity.status(403).body("Account deactivated");
    }
    return ResponseEntity.ok().build();
}

}

