package backend;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.io.Serializable;

public class Account implements Serializable {
    private String accountNumber;
    private double balance;
    private Customer customer;
    private List<Transaction> transactionHistory;
    private String debitCardNumber;
    private String creditCardNumber;

    public Account(String accountNumber, Customer customer, double initialDeposit) {
        this.accountNumber = accountNumber;
        this.customer = customer;
        this.balance = initialDeposit;
        this.transactionHistory = new ArrayList<>();
        
        if (initialDeposit > 0) {
            String txId = UUID.randomUUID().toString().substring(0, 8);
            transactionHistory.add(new Transaction(txId, "Initial Deposit", initialDeposit));
        }
    }

    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }
    public Customer getCustomer() { return customer; }
    public String getDebitCardNumber() { return debitCardNumber; }
    public String getCreditCardNumber() { return creditCardNumber; }
    
    public String deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            String txId = UUID.randomUUID().toString().substring(0, 8);
            transactionHistory.add(new Transaction(txId, "Deposit", amount));
            return "Successfully deposited $" + amount;
        } else {
            return "Deposit amount must be positive.";
        }
    }
    
    public String withdraw(double amount) {
        if (amount > 0 && balance >= amount) {
            balance -= amount;
            String txId = UUID.randomUUID().toString().substring(0, 8);
            transactionHistory.add(new Transaction(txId, "Withdrawal", amount));
            return "Successfully withdrew $" + amount;
        } else if (amount <= 0) {
            return "Withdrawal amount must be positive.";
        } else {
            return "Insufficient funds. Current balance is $" + balance;
        }
    }
    
    public String applyLoan(double amount, String loanType) {
        if (amount > 0) {
            balance += amount;
            String txId = UUID.randomUUID().toString().substring(0, 8);
            transactionHistory.add(new Transaction(txId, loanType + " Disbursed", amount));
            return loanType + " of $" + amount + " successfully approved and disbursed!";
        } else {
            return "Loan amount must be positive.";
        }
    }
    
    private String generateCardNumber() {
        StringBuilder cardNum = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            cardNum.append(String.format("%04d", (int)(Math.random() * 10000)));
            if (i < 3) cardNum.append("-");
        }
        return cardNum.toString();
    }

    public String applyDebitCard() {
        if (debitCardNumber != null) {
            return "You already have a Debit Card.";
        }
        debitCardNumber = generateCardNumber();
        return "Debit Card successfully issued! Number: " + debitCardNumber;
    }

    public String applyCreditCard() {
        if (creditCardNumber != null) {
            return "You already have a Credit Card.";
        }
        creditCardNumber = generateCardNumber();
        return "Credit Card successfully issued! Number: " + creditCardNumber;
    }
    
    public List<Transaction> getTransactionHistory() {
        return transactionHistory;
    }
    
    public String getStatement() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Account Statement ---\n");
        sb.append("Account Number: ").append(accountNumber).append("\n");
        sb.append("Customer: ").append(customer.getName()).append("\n");
        if (debitCardNumber != null) {
            sb.append("Debit Card: ").append(debitCardNumber).append("\n");
        }
        if (creditCardNumber != null) {
            sb.append("Credit Card: ").append(creditCardNumber).append("\n");
        }
        sb.append("Current Balance: $").append(String.format("%.2f", balance)).append("\n");
        sb.append("Transaction History:\n");
        if (transactionHistory.isEmpty()) {
            sb.append("No transactions found.\n");
        } else {
            for (Transaction t : transactionHistory) {
                sb.append(t.toString()).append("\n");
            }
        }
        sb.append("-------------------------");
        return sb.toString();
    }
}
