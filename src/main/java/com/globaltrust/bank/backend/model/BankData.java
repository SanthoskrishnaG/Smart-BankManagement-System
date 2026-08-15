package com.globaltrust.bank.backend.model;

import java.util.ArrayList;
import java.util.List;

public class BankData {
    private Admin admin;
    private List<User> users;

    public BankData() {
        this.users = new ArrayList<>();
    }

    public BankData(Admin admin, List<User> users) {
        this.admin = admin;
        this.users = users != null ? users : new ArrayList<>();
    }

    public Admin getAdmin() { return admin; }
    public void setAdmin(Admin admin) { this.admin = admin; }
    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }
}
