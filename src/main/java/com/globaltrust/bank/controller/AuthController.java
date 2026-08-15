package com.globaltrust.bank.controller;

import com.globaltrust.bank.model.Admin;
import com.globaltrust.bank.model.User;
import com.globaltrust.bank.service.BankDataService;
import com.globaltrust.bank.session.SessionManager;

public class AuthController {

    private BankDataService bankDataService = BankDataService.getInstance();

    public boolean loginAdmin(String username, String password) {
        Admin admin = bankDataService.authenticateAdmin(username, password);
        if (admin != null) {
            SessionManager.loginAdmin(admin);
            return true;
        }
        return false;
    }

    public boolean loginUser(String username, String password) {
        User user = bankDataService.authenticateUser(username, password);
        if (user != null) {
            SessionManager.loginUser(user);
            return true;
        }
        return false;
    }

    public boolean register(User user) {
        User registered = bankDataService.registerUser(user);
        return registered != null;
    }

    public void logout() {
        SessionManager.logout();
    }
}
