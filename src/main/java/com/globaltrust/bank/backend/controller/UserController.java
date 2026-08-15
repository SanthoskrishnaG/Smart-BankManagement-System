package com.globaltrust.bank.backend.controller;

import com.globaltrust.bank.backend.model.Account;
import com.globaltrust.bank.backend.model.Loan;
import com.globaltrust.bank.backend.model.Transaction;
import com.globaltrust.bank.backend.model.User;
import com.globaltrust.bank.backend.service.BankDataService;
import com.globaltrust.bank.backend.session.SessionManager;

public class UserController {

    private BankDataService bankDataService = BankDataService.getInstance();

    private String getUserId() {
        if (SessionManager.isUserLoggedIn()) {
            return SessionManager.getCurrentUser().getId();
        }
        throw new RuntimeException("Unauthorized");
    }

    public User getProfile() {
        return bankDataService.getUserById(getUserId());
    }

    public Account createAccount(String type) {
        if (type == null || type.isEmpty()) {
            type = "Savings";
        }
        return bankDataService.createAccount(getUserId(), type);
    }

    public Transaction deposit(String accountNumber, double amount) {
        return bankDataService.deposit(getUserId(), accountNumber, amount);
    }

    public Transaction withdraw(String accountNumber, double amount) {
        return bankDataService.withdraw(getUserId(), accountNumber, amount);
    }

    public Loan applyLoan(double amount, String duration, String reason) {
        return bankDataService.applyLoan(getUserId(), amount, duration, reason);
    }
}
