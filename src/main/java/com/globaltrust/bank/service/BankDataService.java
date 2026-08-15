package com.globaltrust.bank.service;

import com.globaltrust.bank.model.Account;
import com.globaltrust.bank.model.Admin;
import com.globaltrust.bank.model.BankData;
import com.globaltrust.bank.model.Loan;
import com.globaltrust.bank.model.Transaction;
import com.globaltrust.bank.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BankDataService {

    private static final String DATA_FILE = "data.json";
    private Gson gson;
    private BankData bankData;

    private static BankDataService instance;

    public static synchronized BankDataService getInstance() {
        if (instance == null) {
            instance = new BankDataService();
        }
        return instance;
    }

    private BankDataService() {
        gson = new GsonBuilder().setPrettyPrinting().create();
        loadData();
    }

    private synchronized void loadData() {
        try {
            File file = new File(DATA_FILE);
            if (!file.exists()) {
                try (InputStream is = getClass().getClassLoader().getResourceAsStream(DATA_FILE)) {
                    if (is != null) {
                        try (FileOutputStream os = new FileOutputStream(file)) {
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = is.read(buffer)) > 0) {
                                os.write(buffer, 0, length);
                            }
                        }
                    } else {
                        // If file is not found in resources, just create an empty one or default
                        bankData = new BankData(new Admin("admin", "admin123"), new ArrayList<>());
                        saveData();
                        return;
                    }
                }
            }

            try (Reader reader = new FileReader(file)) {
                bankData = gson.fromJson(reader, BankData.class);
            }
            if (bankData == null) {
                bankData = new BankData(new Admin("admin", "admin123"), new ArrayList<>());
                saveData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            bankData = new BankData(new Admin("admin", "admin123"), new ArrayList<>());
        }
    }

    private synchronized void saveData() {
        try (Writer writer = new FileWriter(DATA_FILE)) {
            gson.toJson(bankData, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized User authenticateUser(String username, String password) {
        return bankData.getUsers().stream()
                .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    public synchronized Admin authenticateAdmin(String username, String password) {
        if (bankData.getAdmin() != null &&
            bankData.getAdmin().getUsername().equals(username) &&
            bankData.getAdmin().getPassword().equals(password)) {
            return bankData.getAdmin();
        }
        return null;
    }

    public synchronized User registerUser(User user) {
        if (bankData.getUsers().stream().anyMatch(u -> u.getUsername().equals(user.getUsername()))) {
            return null;
        }
        user.setId("U" + UUID.randomUUID().toString().substring(0, 5).toUpperCase());
        if (user.getAccounts() == null) user.setAccounts(new ArrayList<>());
        if (user.getLoans() == null) user.setLoans(new ArrayList<>());

        bankData.getUsers().add(user);
        saveData();
        return user;
    }

    public synchronized User getUserById(String id) {
        return bankData.getUsers().stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
    }

    public synchronized Account createAccount(String userId, String type) {
        User user = getUserById(userId);
        if (user == null) return null;

        String accNumber = "ACC" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Account newAccount = new Account(accNumber, type, 0.0, new ArrayList<>());
        user.getAccounts().add(newAccount);
        saveData();
        return newAccount;
    }

    public synchronized Transaction deposit(String userId, String accountNumber, double amount) {
        User user = getUserById(userId);
        if (user == null || amount <= 0) return null;

        Optional<Account> accountOpt = user.getAccounts().stream()
                .filter(a -> a.getAccountNumber().equals(accountNumber))
                .findFirst();

        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            account.setBalance(account.getBalance() + amount);

            Transaction tx = new Transaction(
                "TXN" + UUID.randomUUID().toString().substring(0, 6).toUpperCase(),
                "Deposit", amount, LocalDate.now().toString(), "Deposit"
            );
            account.getTransactions().add(tx);
            saveData();
            return tx;
        }
        return null;
    }

    public synchronized Transaction withdraw(String userId, String accountNumber, double amount) {
        User user = getUserById(userId);
        if (user == null || amount <= 0) return null;

        Optional<Account> accountOpt = user.getAccounts().stream()
                .filter(a -> a.getAccountNumber().equals(accountNumber))
                .findFirst();

        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            if (account.getBalance() >= amount) {
                account.setBalance(account.getBalance() - amount);

                Transaction tx = new Transaction(
                    "TXN" + UUID.randomUUID().toString().substring(0, 6).toUpperCase(),
                    "Withdrawal", amount, LocalDate.now().toString(), "Withdrawal"
                );
                account.getTransactions().add(tx);
                saveData();
                return tx;
            }
        }
        return null;
    }

    public synchronized Loan applyLoan(String userId, double amount, String duration, String reason) {
        User user = getUserById(userId);
        if (user == null || amount <= 0) return null;

        Loan loan = new Loan(
            "L" + UUID.randomUUID().toString().substring(0, 5).toUpperCase(),
            amount, duration, reason, "Pending"
        );
        user.getLoans().add(loan);
        saveData();
        return loan;
    }

    public synchronized List<User> getAllUsers() {
        return new ArrayList<>(bankData.getUsers());
    }

    public synchronized boolean deleteUser(String userId) {
        boolean removed = bankData.getUsers().removeIf(u -> u.getId().equals(userId));
        if (removed) {
            saveData();
        }
        return removed;
    }

    public synchronized boolean updateLoanStatus(String loanId, String status) {
        for (User user : bankData.getUsers()) {
            for (Loan loan : user.getLoans()) {
                if (loan.getLoanId().equals(loanId)) {
                    loan.setStatus(status);

                    if ("Approved".equals(status) && !user.getAccounts().isEmpty()) {
                        Account acc = user.getAccounts().get(0);
                        acc.setBalance(acc.getBalance() + loan.getAmount());
                        acc.getTransactions().add(new Transaction(
                             "TXN" + UUID.randomUUID().toString().substring(0, 6).toUpperCase(),
                             "Loan Disbursement", loan.getAmount(), LocalDate.now().toString(), "Loan Approved: " + loanId
                        ));
                    }
                    saveData();
                    return true;
                }
            }
        }
        return false;
    }

    public synchronized List<Transaction> getAllTransactions() {
        List<Transaction> allTx = new ArrayList<>();
        for (User u : bankData.getUsers()) {
            for (Account a : u.getAccounts()) {
                allTx.addAll(a.getTransactions());
            }
        }
        return allTx;
    }
}
