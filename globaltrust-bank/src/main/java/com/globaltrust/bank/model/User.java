package com.globaltrust.bank.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String id;
    private String name;
    private String username;
    private String password;
    private List<Account> accounts;
    private List<Loan> loans;

    public User() {
        this.accounts = new ArrayList<>();
        this.loans = new ArrayList<>();
    }

    public User(String id, String name, String username, String password, List<Account> accounts, List<Loan> loans) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.accounts = accounts != null ? accounts : new ArrayList<>();
        this.loans = loans != null ? loans : new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public List<Loan> getLoans() {
        return loans;
    }

    public void setLoans(List<Loan> loans) {
        this.loans = loans;
    }
}
