package com.globaltrust.bank.backend.controller;

import com.globaltrust.bank.backend.model.Transaction;
import com.globaltrust.bank.backend.model.User;
import com.globaltrust.bank.backend.service.BankDataService;
import com.globaltrust.bank.backend.session.SessionManager;

import java.util.List;

public class AdminController {

    private BankDataService bankDataService = BankDataService.getInstance();

    private void checkAdmin() {
        if (!SessionManager.isAdminLoggedIn()) {
            throw new RuntimeException("Unauthorized: Admin access required");
        }
    }

    public List<User> getAllUsers() {
        checkAdmin();
        return bankDataService.getAllUsers();
    }

    public User addUser(User user) {
        checkAdmin();
        return bankDataService.registerUser(user);
    }

    public boolean deleteUser(String id) {
        checkAdmin();
        return bankDataService.deleteUser(id);
    }

    public boolean approveLoan(String loanId) {
        checkAdmin();
        return bankDataService.updateLoanStatus(loanId, "Approved");
    }

    public boolean rejectLoan(String loanId) {
        checkAdmin();
        return bankDataService.updateLoanStatus(loanId, "Rejected");
    }

    public List<Transaction> getAllTransactions() {
        checkAdmin();
        return bankDataService.getAllTransactions();
    }
}
