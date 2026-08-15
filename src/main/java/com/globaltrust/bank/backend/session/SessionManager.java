package com.globaltrust.bank.backend.session;

import com.globaltrust.bank.backend.model.Admin;
import com.globaltrust.bank.backend.model.User;

public class SessionManager {
    
    private static User currentUser;
    private static Admin currentAdmin;

    public static void loginUser(User user) {
        currentUser = user;
        currentAdmin = null;
    }

    public static void loginAdmin(Admin admin) {
        currentAdmin = admin;
        currentUser = null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static Admin getCurrentAdmin() {
        return currentAdmin;
    }

    public static void logout() {
        currentUser = null;
        currentAdmin = null;
    }

    public static boolean isUserLoggedIn() {
        return currentUser != null;
    }

    public static boolean isAdminLoggedIn() {
        return currentAdmin != null;
    }
}
