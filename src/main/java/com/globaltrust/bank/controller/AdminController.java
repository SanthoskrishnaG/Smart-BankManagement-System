package com.globaltrust.bank.controller;

import com.globaltrust.bank.model.Transaction;
import com.globaltrust.bank.model.User;
import com.globaltrust.bank.service.BankDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private BankDataService bankDataService;

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = bankDataService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/users")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        User registered = bankDataService.registerUser(user);
        if (registered != null) {
            return ResponseEntity.ok(registered);
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Username already exists"));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        boolean deleted = bankDataService.deleteUser(id);
        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "User deleted"));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Failed to delete user"));
    }

    @PutMapping("/loans/{loanId}/approve")
    public ResponseEntity<?> approveLoan(@PathVariable String loanId) {
        boolean success = bankDataService.updateLoanStatus(loanId, "Approved");
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Loan approved"));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Failed to approve loan"));
    }

    @PutMapping("/loans/{loanId}/reject")
    public ResponseEntity<?> rejectLoan(@PathVariable String loanId) {
        boolean success = bankDataService.updateLoanStatus(loanId, "Rejected");
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Loan rejected"));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Failed to reject loan"));
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> getAllTransactions() {
        List<Transaction> transactions = bankDataService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }
}
