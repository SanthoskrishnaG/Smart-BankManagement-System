package com.globaltrust.bank.model;

import java.util.ArrayList;
import java.util.List;

public class Account {
    private String accountNumber;
    private String type;
    private double balance;
    private List<Transaction> transactions;

    public Account() {
        this.transactions = new ArrayList<>();
    }

    public Account(String accountNumber, String type, double balance, List<Transaction> transactions) {
        this.accountNumber = accountNumber;
        this.type = type;
        this.balance = balance;
        this.transactions = transactions != null ? transactions : new ArrayList<>();
    }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public List<Transaction> getTransactions() { return transactions; }
    public void setTransactions(List<Transaction> transactions) { this.transactions = transactions; }
}
