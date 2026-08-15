package com.globaltrust.bank.controller;

import com.globaltrust.bank.model.Account;
import com.globaltrust.bank.model.Loan;
import com.globaltrust.bank.model.Transaction;
import com.globaltrust.bank.service.BankDataService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private BankDataService bankDataService;

    private String getUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            return (String) session.getAttribute("userId");
        }
        throw new RuntimeException("Unauthorized");
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        String userId = getUserId(request);
        com.globaltrust.bank.model.User user = bankDataService.getUserById(userId);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
    }

    @PostMapping("/account")
    public ResponseEntity<?> createAccount(HttpServletRequest request, @RequestBody Map<String, String> body) {
        String userId = getUserId(request);
        String type = body.getOrDefault("type", "Savings");
        Account acc = bankDataService.createAccount(userId, type);
        if (acc != null) {
            return ResponseEntity.ok(acc);
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Failed to create account"));
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(HttpServletRequest request, @RequestBody Map<String, Object> body) {
        String userId = getUserId(request);
        String accountNumber = (String) body.get("accountNumber");
        double amount = Double.parseDouble(body.get("amount").toString());

        Transaction tx = bankDataService.deposit(userId, accountNumber, amount);
        if (tx != null) {
            return ResponseEntity.ok(tx);
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Deposit failed. Check account number and amount."));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(HttpServletRequest request, @RequestBody Map<String, Object> body) {
        String userId = getUserId(request);
        String accountNumber = (String) body.get("accountNumber");
        double amount = Double.parseDouble(body.get("amount").toString());

        Transaction tx = bankDataService.withdraw(userId, accountNumber, amount);
        if (tx != null) {
            return ResponseEntity.ok(tx);
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Withdrawal failed. Insufficient funds or invalid account."));
    }

    @PostMapping("/loan")
    public ResponseEntity<?> applyLoan(HttpServletRequest request, @RequestBody Map<String, Object> body) {
        String userId = getUserId(request);
        double amount = Double.parseDouble(body.get("amount").toString());
        String duration = (String) body.get("duration");
        String reason = (String) body.get("reason");

        Loan loan = bankDataService.applyLoan(userId, amount, duration, reason);
        if (loan != null) {
            return ResponseEntity.ok(loan);
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Loan application failed"));
    }
}
