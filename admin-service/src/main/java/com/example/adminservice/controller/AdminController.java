package com.example.adminservice.controller;

import com.example.adminservice.models.Expense;
import com.example.adminservice.models.User;
import com.example.adminservice.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Value("${admin.secret:change-me-secret}")
    private String adminSecret;

    // simple header auth: clients must send X-Admin-Auth header equal to admin.secret
    private boolean isAuthorized(String header) {
        return header != null && header.equals(adminSecret);
    }

    @GetMapping("/users")
    public ResponseEntity<?> allUsers(@RequestHeader(value="X-Admin-Auth", required=false) String auth) {
        if (!isAuthorized(auth)) return ResponseEntity.status(401).body("Unauthorized");
        List<User> users = adminService.allUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUser(@RequestHeader(value="X-Admin-Auth", required=false) String auth,
                                     @PathVariable Long id) {
        if (!isAuthorized(auth)) return ResponseEntity.status(401).body("Unauthorized");
        return ResponseEntity.ok(adminService.getUser(id));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@RequestHeader(value="X-Admin-Auth", required=false) String auth,
                                        @PathVariable Long id, @RequestBody User user) {
        if (!isAuthorized(auth)) return ResponseEntity.status(401).body("Unauthorized");
        int r = adminService.updateUser(id, user.getEmail(), user.getIsActive());
        return ResponseEntity.ok(r);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@RequestHeader(value="X-Admin-Auth", required=false) String auth,
                                        @PathVariable Long id) {
        if (!isAuthorized(auth)) return ResponseEntity.status(401).body("Unauthorized");
        int r = adminService.deleteUser(id);
        return ResponseEntity.ok(r);
    }

    @PostMapping("/users/{id}/deactivate")
    public ResponseEntity<?> setActive(@RequestHeader(value="X-Admin-Auth", required=false) String auth,
                                       @PathVariable Long id, @RequestParam boolean active) {
        if (!isAuthorized(auth)) return ResponseEntity.status(401).body("Unauthorized");
        int r = adminService.setActive(id, active);
        return ResponseEntity.ok(r);
    }

    @GetMapping("/users/{id}/expenses")
    public ResponseEntity<?> getUserExpenses(@RequestHeader(value="X-Admin-Auth", required=false) String auth,
                                             @PathVariable Long id) {
        if (!isAuthorized(auth)) return ResponseEntity.status(401).body("Unauthorized");
        List<Expense> e = adminService.getUserExpenses(id);
        return ResponseEntity.ok(e);
    }

    @PutMapping("/expenses/{id}")
    public ResponseEntity<?> updateExpense(@RequestHeader(value="X-Admin-Auth", required=false) String auth,
                                           @PathVariable Long id, @RequestBody Expense expense) {
        if (!isAuthorized(auth)) return ResponseEntity.status(401).body("Unauthorized");
        int r = adminService.updateExpense(id, expense);
        return ResponseEntity.ok(r);
    }

    @DeleteMapping("/expenses/{id}")
    public ResponseEntity<?> deleteExpense(@RequestHeader(value="X-Admin-Auth", required=false) String auth,
                                           @PathVariable Long id) {
        if (!isAuthorized(auth)) return ResponseEntity.status(401).body("Unauthorized");
        int r = adminService.deleteExpense(id);
        return ResponseEntity.ok(r);
    }
}
