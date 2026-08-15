package com.globaltrust.bank.controller;

import com.globaltrust.bank.model.Admin;
import com.globaltrust.bank.model.User;
import com.globaltrust.bank.service.BankDataService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private BankDataService bankDataService;

    static class LoginRequest {
        public String username;
        public String password;
        public String role;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req, HttpServletRequest request) {
        if ("admin".equalsIgnoreCase(req.role)) {
            Admin admin = bankDataService.authenticateAdmin(req.username, req.password);
            if (admin != null) {
                HttpSession session = request.getSession(true);
                session.setAttribute("adminId", admin.getUsername());
                Map<String, String> response = new HashMap<>();
                response.put("message", "Admin logged in successfully");
                response.put("role", "admin");
                return ResponseEntity.ok(response);
            }
        } else {
            User user = bankDataService.authenticateUser(req.username, req.password);
            if (user != null) {
                HttpSession session = request.getSession(true);
                session.setAttribute("userId", user.getId());
                Map<String, Object> response = new HashMap<>();
                response.put("message", "User logged in successfully");
                response.put("role", "user");
                response.put("userId", user.getId());
                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        User registered = bankDataService.registerUser(user);
        if (registered != null) {
            return ResponseEntity.ok(registered);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Username already exists"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @GetMapping("/session")
    public ResponseEntity<?> checkSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            if (session.getAttribute("userId") != null) {
                return ResponseEntity.ok(Map.of("role", "user", "userId", session.getAttribute("userId")));
            }
            if (session.getAttribute("adminId") != null) {
                return ResponseEntity.ok(Map.of("role", "admin"));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No active session"));
    }
}
