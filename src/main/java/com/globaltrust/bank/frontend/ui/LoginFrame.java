package com.globaltrust.bank.frontend.ui;

import com.globaltrust.bank.backend.controller.AuthController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {
    
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private AuthController authController;
    private JRadioButton rbUser;
    private JRadioButton rbAdmin;

    public LoginFrame() {
        authController = new AuthController();
        setTitle("GlobalTrust Bank - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initUI();
    }
    
    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel lblTitle = new JLabel("GlobalTrust Bank", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        panel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        txtUsername = new JTextField(15);
        panel.add(txtUsername, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        txtPassword = new JPasswordField(15);
        panel.add(txtPassword, gbc);
        
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        JPanel rbPanel = new JPanel();
        rbUser = new JRadioButton("User", true);
        rbAdmin = new JRadioButton("Admin");
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbUser); bg.add(rbAdmin);
        rbPanel.add(rbUser); rbPanel.add(rbAdmin);
        panel.add(rbPanel, gbc);

        gbc.gridy++;
        JButton btnLogin = new JButton("Login");
        btnLogin.addActionListener(this::handleLogin);
        panel.add(btnLogin, gbc);

        add(panel);
    }
    
    private void handleLogin(ActionEvent e) {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success;
        if (rbAdmin.isSelected()) {
            success = authController.loginAdmin(username, password);
            if (success) {
                new AdminDashboardFrame().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Admin Credentials.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            success = authController.loginUser(username, password);
            if (success) {
                new UserDashboardFrame().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid User Credentials.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
