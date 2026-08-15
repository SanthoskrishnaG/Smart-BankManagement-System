package com.globaltrust.bank.frontend.ui;

import com.globaltrust.bank.backend.controller.AdminController;
import com.globaltrust.bank.backend.controller.AuthController;
import com.globaltrust.bank.backend.model.Transaction;
import com.globaltrust.bank.backend.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboardFrame extends JFrame {

    private AdminController adminController;
    private AuthController authController;

    public AdminDashboardFrame() {
        adminController = new AdminController();
        authController = new AuthController();

        setTitle("Admin Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.add("Users", createUsersPanel());
        tabbedPane.add("All Transactions", createTransactionsPanel());

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

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        List<User> users = adminController.getAllUsers();
        
        String[] columns = {"ID", "Username", "Name"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (User user : users) {
            model.addRow(new Object[]{user.getId(), user.getUsername(), user.getName()});
        }

        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        List<Transaction> transactions = adminController.getAllTransactions();
        
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
