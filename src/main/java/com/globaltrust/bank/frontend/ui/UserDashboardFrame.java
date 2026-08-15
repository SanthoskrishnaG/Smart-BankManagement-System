package com.globaltrust.bank.frontend.ui;

import com.globaltrust.bank.backend.controller.AuthController;
import com.globaltrust.bank.backend.controller.UserController;
import com.globaltrust.bank.backend.model.Account;
import com.globaltrust.bank.backend.model.Transaction;
import com.globaltrust.bank.backend.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserDashboardFrame extends JFrame {

    private UserController userController;
    private AuthController authController;
    private User currentUser;

    public UserDashboardFrame() {
        userController = new UserController();
        authController = new AuthController();
        currentUser = userController.getProfile();

        setTitle("User Dashboard - " + currentUser.getUsername());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.add("Summary", createSummaryPanel());
        tabbedPane.add("Transactions", createTransactionsPanel());

        add(tabbedPane, BorderLayout.CENTER);

        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> {
            authController.logout();
            new LoginFrame().setVisible(true);
            dispose();
        });
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(btnLogout);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        if (currentUser.getAccounts().isEmpty()) {
            panel.add(new JLabel("No accounts found. Please create one."), BorderLayout.NORTH);
            JButton btnCreate = new JButton("Create Savings Account");
            btnCreate.addActionListener(e -> {
                userController.createAccount("Savings");
                JOptionPane.showMessageDialog(this, "Account created successfully. Please login again.");
            });
            panel.add(btnCreate, BorderLayout.CENTER);
            return panel;
        }

        Account account = currentUser.getAccounts().get(0);

        JPanel detailsPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        detailsPanel.add(new JLabel("Account Number:"));
        detailsPanel.add(new JLabel(account.getAccountNumber()));
        detailsPanel.add(new JLabel("Account Type:"));
        detailsPanel.add(new JLabel(account.getType()));
        detailsPanel.add(new JLabel("Balance:"));
        detailsPanel.add(new JLabel(String.format("$%.2f", account.getBalance())));

        panel.add(detailsPanel, BorderLayout.NORTH);

        JPanel actionPanel = new JPanel();
        JButton btnDeposit = new JButton("Deposit");
        JButton btnWithdraw = new JButton("Withdraw");
        
        btnDeposit.addActionListener(e -> handleDeposit(account.getAccountNumber()));
        btnWithdraw.addActionListener(e -> handleWithdraw(account.getAccountNumber()));

        actionPanel.add(btnDeposit);
        actionPanel.add(btnWithdraw);

        panel.add(actionPanel, BorderLayout.CENTER);

        return panel;
    }

    private void handleDeposit(String accountNumber) {
        String amountStr = JOptionPane.showInputDialog(this, "Enter deposit amount:");
        try {
            double amount = Double.parseDouble(amountStr);
            if (amount > 0) {
                userController.deposit(accountNumber, amount);
                JOptionPane.showMessageDialog(this, "Deposit successful. Please login again to see updated balance.");
            } else {
                JOptionPane.showMessageDialog(this, "Amount must be positive.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount.");
        } catch (NullPointerException ex) {
            // Ignored, user cancelled
        }
    }

    private void handleWithdraw(String accountNumber) {
        String amountStr = JOptionPane.showInputDialog(this, "Enter withdrawal amount:");
        try {
            double amount = Double.parseDouble(amountStr);
            if (amount > 0) {
                userController.withdraw(accountNumber, amount);
                JOptionPane.showMessageDialog(this, "Withdrawal successful. Please login again to see updated balance.");
            } else {
                JOptionPane.showMessageDialog(this, "Amount must be positive.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount.");
        } catch (NullPointerException ex) {
            // Ignored, user cancelled
        }
    }

    private JPanel createTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        if (currentUser.getAccounts().isEmpty()) {
            return panel;
        }

        List<Transaction> transactions = currentUser.getAccounts().get(0).getTransactions();
        
        String[] columns = {"ID", "Date", "Type", "Amount", "Description"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (Transaction tx : transactions) {
            model.addRow(new Object[]{tx.getId(), tx.getDate(), tx.getType(), tx.getAmount(), tx.getDescription()});
        }

        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }
}
