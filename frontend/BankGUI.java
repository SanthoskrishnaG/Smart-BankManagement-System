package frontend;

import backend.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BankGUI extends JFrame {
    private Bank bank;
    private Account loggedInUser = null;

    private JPanel rootCardPanel;
    private CardLayout rootCardLayout;

    private JPanel dashboardPanel;
    private JPanel dashboardSidebar;
    private JPanel dashboardContent;
    private CardLayout dashboardContentLayout;

    // Professional Color Palette
    private final Color primaryColor = new Color(37, 99, 235); // Blue 600
    private final Color primaryHoverColor = new Color(29, 78, 216); // Blue 700
    private final Color sidebarColor = new Color(15, 23, 42); // Slate 900
    private final Color sidebarHoverColor = new Color(30, 41, 59); // Slate 800
    private final Color bgColor = new Color(248, 250, 252); // Slate 50
    private final Color textColor = new Color(30, 41, 59);

    public BankGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}

        bank = new Bank(); // This automatically loads data from backend/backend_data.dat
        setTitle("Global Trust Bank");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        rootCardLayout = new CardLayout();
        rootCardPanel = new JPanel(rootCardLayout);

        rootCardPanel.add(createLoginSelectionPanel(), "LoginSelection");
        rootCardPanel.add(createUserLoginPanel(), "UserLogin");
        rootCardPanel.add(createRegisterPanel(), "Register");
        
        dashboardPanel = new JPanel(new BorderLayout());
        dashboardSidebar = new JPanel();
        dashboardSidebar.setLayout(new BoxLayout(dashboardSidebar, BoxLayout.Y_AXIS));
        dashboardSidebar.setBackground(sidebarColor);
        dashboardSidebar.setPreferredSize(new Dimension(280, getHeight()));
        dashboardSidebar.setBorder(new EmptyBorder(20, 15, 20, 15));

        dashboardContentLayout = new CardLayout();
        dashboardContent = new JPanel(dashboardContentLayout);
        dashboardContent.setBackground(bgColor);

        dashboardPanel.add(dashboardSidebar, BorderLayout.WEST);
        dashboardPanel.add(dashboardContent, BorderLayout.CENTER);

        rootCardPanel.add(dashboardPanel, "Dashboard");

        add(rootCardPanel);
        setVisible(true);
    }

    // --- LOGIN FLOW PANELS ---

    private JPanel createLoginSelectionPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(bgColor);

        JLabel title = new JLabel("Global Trust Bank");
        title.setFont(new Font("Segoe UI", Font.BOLD, 48));
        title.setForeground(sidebarColor);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Secure, Fast, and Reliable Banking");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton loginBtn = createPrimaryButton("Login to Account");
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(250, 50));
        loginBtn.addActionListener(e -> rootCardLayout.show(rootCardPanel, "UserLogin"));

        JButton registerBtn = createPrimaryButton("Open New Account");
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerBtn.setMaximumSize(new Dimension(250, 50));
        registerBtn.addActionListener(e -> rootCardLayout.show(rootCardPanel, "Register"));

        panel.add(Box.createVerticalGlue());
        panel.add(title);
        panel.add(Box.createVerticalStrut(10));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(50));
        panel.add(loginBtn);
        panel.add(Box.createVerticalStrut(20));
        panel.add(registerBtn);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createUserLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(bgColor);
        
        JPanel form = new JPanel(new GridLayout(5, 1, 10, 10));
        form.setBackground(bgColor);
        form.setPreferredSize(new Dimension(350, 300));

        JLabel title = new JLabel("Secure Login", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(sidebarColor);
        
        RoundedTextField field1 = new RoundedTextField();
        JPasswordField field2 = new JPasswordField();
        stylePasswordField(field2);

        form.add(title);
        form.add(createFormLabel("Full Name:"));
        form.add(field1);
        form.add(createFormLabel("4-Digit PIN:"));
        form.add(field2);

        JButton loginBtn = createPrimaryButton("Login");
        JButton backBtn = new JButton("Back");
        backBtn.setFocusPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.setForeground(primaryColor);
        backBtn.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        loginBtn.addActionListener(e -> {
            String val1 = field1.getText().trim();
            String val2 = new String(field2.getPassword()).trim();
            
            Account acc = bank.authenticateUser(val1, val2);
            if (acc != null) {
                field1.setText("");
                field2.setText("");
                setupUserDashboard(acc);
            } else {
                showError("Invalid User credentials. Please check your name and PIN.");
            }
        });

        backBtn.addActionListener(e -> {
            field1.setText("");
            field2.setText("");
            rootCardLayout.show(rootCardPanel, "LoginSelection");
        });

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
        btnPanel.setBackground(bgColor);
        btnPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPanel.add(loginBtn);
        btnPanel.add(backBtn);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(bgColor);
        wrapper.add(form, BorderLayout.CENTER);
        wrapper.add(btnPanel, BorderLayout.SOUTH);

        panel.add(wrapper);
        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);
        panel.setBorder(new EmptyBorder(50, 80, 50, 80));

        JLabel title = new JLabel("Open New Account (OTP Secured)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(sidebarColor);
        title.setBorder(new EmptyBorder(0, 0, 30, 0));
        panel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 20, 20));
        formPanel.setBackground(bgColor);

        RoundedTextField nameField = new RoundedTextField();
        RoundedTextField emailField = new RoundedTextField();
        RoundedTextField phoneField = new RoundedTextField();
        RoundedTextField pinField = new RoundedTextField();
        RoundedTextField depositField = new RoundedTextField();

        formPanel.add(createFormLabel("Full Name:"));
        formPanel.add(nameField);
        formPanel.add(createFormLabel("Email Address:"));
        formPanel.add(emailField);
        formPanel.add(createFormLabel("Phone Number:"));
        formPanel.add(phoneField);
        formPanel.add(createFormLabel("Secure 4-Digit PIN:"));
        formPanel.add(pinField);
        formPanel.add(createFormLabel("Initial Deposit ($):"));
        formPanel.add(depositField);

        JButton submitBtn = createPrimaryButton("Register Account");
        JButton backBtn = new JButton("Back");
        backBtn.setFocusPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.setForeground(primaryColor);
        backBtn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonPanel.setBackground(bgColor);
        buttonPanel.setBorder(new EmptyBorder(40, 0, 0, 0));
        buttonPanel.add(submitBtn);
        buttonPanel.add(backBtn);

        submitBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String phone = phoneField.getText().trim();
                String pin = pinField.getText().trim();
                String depositStr = depositField.getText().trim();
                
                if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || pin.isEmpty() || depositStr.isEmpty()) {
                    showError("Please fill out all fields.");
                    return;
                }
                
                if (pin.length() != 4 || !pin.matches("\\d+")) {
                    showError("PIN must be exactly 4 digits.");
                    return;
                }

                double deposit = Double.parseDouble(depositStr);
                
                if (deposit < 0) {
                    showError("Initial deposit cannot be negative.");
                    return;
                }
                
                // OTP Verification Step
                String generatedOtp = String.format("%06d", (int)(Math.random() * 1000000));
                boolean sent = backend.EmailService.sendOTP(email, generatedOtp);
                
                if (!sent) {
                    showError("Failed to send OTP to " + email + ". Please check the email address or the SMTP configuration.");
                    return;
                }

                String enteredOtp = JOptionPane.showInputDialog(panel, "An OTP has been sent to " + email + ".\nPlease enter the 6-digit verification code:", "OTP Verification", JOptionPane.PLAIN_MESSAGE);
                
                if (enteredOtp == null || !enteredOtp.trim().equals(generatedOtp)) {
                    showError("Invalid OTP or verification cancelled. Account registration aborted.");
                    return;
                }
                
                String customerId = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                Customer customer = new Customer(customerId, name, email, phone, pin);
                String accNum = bank.createAccount(customer, deposit);
                
                showSuccess("Account successfully created!\nAccount Number: " + accNum);
                nameField.setText("");
                emailField.setText("");
                phoneField.setText("");
                pinField.setText("");
                depositField.setText("");
                rootCardLayout.show(rootCardPanel, "LoginSelection");
                
            } catch (NumberFormatException ex) {
                showError("Invalid deposit amount.");
            }
        });

        backBtn.addActionListener(e -> rootCardLayout.show(rootCardPanel, "LoginSelection"));
        
        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.setBackground(bgColor);
        centerContainer.add(formPanel, BorderLayout.NORTH);
        centerContainer.add(buttonPanel, BorderLayout.CENTER);

        panel.add(centerContainer, BorderLayout.CENTER);
        return panel;
    }

    private void stylePasswordField(JPasswordField field) {
        field.setOpaque(false);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(203, 213, 225), 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setBackground(Color.WHITE);
    }

    // --- DASHBOARD SETUP ---

    private void setupUserDashboard(Account acc) {
        this.loggedInUser = acc;
        
        dashboardSidebar.removeAll();
        dashboardContent.removeAll();

        addSidebarLogo();
        dashboardSidebar.add(createSidebarButton("Home", "Home"));
        dashboardSidebar.add(Box.createVerticalStrut(10));
        dashboardSidebar.add(createSidebarButton("Deposit", "Deposit"));
        dashboardSidebar.add(Box.createVerticalStrut(10));
        dashboardSidebar.add(createSidebarButton("Withdraw", "Withdraw"));
        dashboardSidebar.add(Box.createVerticalStrut(10));
        dashboardSidebar.add(createSidebarButton("Apply Loan", "Loan"));
        dashboardSidebar.add(Box.createVerticalStrut(10));
        dashboardSidebar.add(createSidebarButton("Manage Cards", "Cards"));
        dashboardSidebar.add(Box.createVerticalStrut(10));
        dashboardSidebar.add(createSidebarButton("Account Info", "Info"));
        dashboardSidebar.add(Box.createVerticalStrut(10));
        dashboardSidebar.add(createSidebarButton("History", "History"));
        dashboardSidebar.add(Box.createVerticalGlue());
        
        JButton logoutBtn = createSidebarButton("Logout", null);
        logoutBtn.addActionListener(e -> {
            this.loggedInUser = null;
            rootCardLayout.show(rootCardPanel, "LoginSelection");
        });
        dashboardSidebar.add(logoutBtn);

        dashboardContent.add(createUserHomePanel(), "Home");
        dashboardContent.add(createUserActionPanel("Deposit Money", "Deposit"), "Deposit");
        dashboardContent.add(createUserActionPanel("Withdraw Money", "Withdraw"), "Withdraw");
        dashboardContent.add(createUserActionPanel("Apply for a Loan", "Loan"), "Loan");
        dashboardContent.add(createCardsPanel(), "Cards");
        dashboardContent.add(createUserActionPanel("Account Information", "Info"), "Info");
        dashboardContent.add(createUserActionPanel("Transaction History", "History"), "History");

        dashboardSidebar.revalidate();
        dashboardSidebar.repaint();
        dashboardContent.revalidate();
        dashboardContent.repaint();

        rootCardLayout.show(rootCardPanel, "Dashboard");
        dashboardContentLayout.show(dashboardContent, "Home");
    }

    private void addSidebarLogo() {
        JLabel logoLabel = new JLabel("Global Trust");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setBorder(new EmptyBorder(10, 0, 40, 0));
        dashboardSidebar.add(logoLabel);
    }

    // --- USER PANELS ---

    private JPanel createUserHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);
        panel.setBorder(new EmptyBorder(60, 60, 60, 60));

        JLabel welcomeLabel = new JLabel("Welcome back, " + (loggedInUser != null ? loggedInUser.getCustomer().getName() : ""));
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        welcomeLabel.setForeground(sidebarColor);
        
        JLabel subLabel = new JLabel("Select an option from the sidebar to manage your account.");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subLabel.setForeground(new Color(100, 116, 139));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(bgColor);
        textPanel.add(Box.createVerticalStrut(150));
        textPanel.add(welcomeLabel);
        textPanel.add(Box.createVerticalStrut(15));
        textPanel.add(subLabel);
        
        panel.add(textPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createUserActionPanel(String titleStr, String actionType) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);
        panel.setBorder(new EmptyBorder(50, 80, 50, 80));

        JLabel title = new JLabel(titleStr);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(sidebarColor);
        title.setBorder(new EmptyBorder(0, 0, 40, 0));
        panel.add(title, BorderLayout.NORTH);

        if (actionType.equals("Info") || actionType.equals("History")) {
            JTextArea textArea = new JTextArea();
            textArea.setFont(new Font("Consolas", Font.PLAIN, 15));
            textArea.setEditable(false);
            textArea.setBorder(new EmptyBorder(15, 15, 15, 15));
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
            panel.add(scrollPane, BorderLayout.CENTER);

            JButton loadBtn = createPrimaryButton(actionType.equals("Info") ? "Load Account Info" : "Load Transaction History");
            loadBtn.addActionListener(e -> {
                if (loggedInUser != null) {
                    textArea.setText(loggedInUser.getStatement());
                }
            });
            
            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
            bottom.setBackground(bgColor);
            bottom.setBorder(new EmptyBorder(20, 0, 0, 0));
            bottom.add(loadBtn);
            panel.add(bottom, BorderLayout.SOUTH);

        } else {
            int rows = 2;
            JPanel formPanel = new JPanel(new GridLayout(rows, 2, 20, 30));
            formPanel.setBackground(bgColor);
            formPanel.setMaximumSize(new Dimension(800, 50 * rows));

            RoundedTextField amountField = new RoundedTextField();
            JComboBox<String> loanTypeCombo = new JComboBox<>(new String[]{"Housing Loan", "Vehicle Loan", "Gold Loan", "Personal Loan"});
            JPasswordField pinField = new JPasswordField();
            stylePasswordField(pinField);

            if (actionType.equals("Loan")) {
                formPanel.add(createFormLabel("Loan Type:"));
                formPanel.add(loanTypeCombo);
                formPanel.add(createFormLabel("Requested Amount ($):"));
                formPanel.add(amountField);
            } else {
                formPanel.add(createFormLabel("Amount ($):"));
                formPanel.add(amountField);
                formPanel.add(createFormLabel("Enter 4-Digit PIN:"));
                formPanel.add(pinField);
            }

            JButton submitBtn = createPrimaryButton("Submit Request");

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            buttonPanel.setBackground(bgColor);
            buttonPanel.setBorder(new EmptyBorder(50, 0, 0, 0));
            buttonPanel.add(submitBtn);

            submitBtn.addActionListener(e -> {
                if (loggedInUser == null) return;

                try {
                    double amount = Double.parseDouble(amountField.getText().trim());
                    String result = "";
                    if (actionType.equals("Deposit")) {
                        String pin = new String(pinField.getPassword()).trim();
                        if (!pin.equals(loggedInUser.getCustomer().getPin())) {
                            showError("Invalid PIN.");
                            return;
                        }
                        result = loggedInUser.deposit(amount);
                    } else if (actionType.equals("Withdraw")) {
                        String pin = new String(pinField.getPassword()).trim();
                        if (!pin.equals(loggedInUser.getCustomer().getPin())) {
                            showError("Invalid PIN.");
                            return;
                        }
                        result = loggedInUser.withdraw(amount);
                    } else if (actionType.equals("Loan")) {
                        String selectedLoan = (String) loanTypeCombo.getSelectedItem();
                        result = loggedInUser.applyLoan(amount, selectedLoan);
                    }
                    
                    // Save state after transaction
                    bank.saveData();

                    showSuccess(result);
                    amountField.setText("");
                    pinField.setText("");
                } catch (NumberFormatException ex) {
                    showError("Invalid amount entered.");
                }
            });
            
            JPanel centerContainer = new JPanel(new BorderLayout());
            centerContainer.setBackground(bgColor);
            centerContainer.add(formPanel, BorderLayout.NORTH);
            centerContainer.add(buttonPanel, BorderLayout.CENTER);

            panel.add(centerContainer, BorderLayout.CENTER);
        }
        return panel;
    }

    // --- USER PANELS --- (Continued)
    private JPanel createCardsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);
        panel.setBorder(new EmptyBorder(50, 80, 50, 80));

        JLabel title = new JLabel("Manage Cards");
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(sidebarColor);
        title.setBorder(new EmptyBorder(0, 0, 40, 0));
        panel.add(title, BorderLayout.NORTH);

        JPanel cardsContentPanel = new JPanel(new GridLayout(2, 1, 0, 40));
        cardsContentPanel.setBackground(bgColor);

        // Debit Card Section
        JPanel debitPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        debitPanel.setBackground(bgColor);
        JLabel debitTitle = new JLabel("Debit Card: ");
        debitTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        debitPanel.add(debitTitle);
        
        if (loggedInUser.getDebitCardNumber() != null) {
            JLabel debitNum = new JLabel(loggedInUser.getDebitCardNumber());
            debitNum.setFont(new Font("Consolas", Font.PLAIN, 20));
            debitPanel.add(debitNum);
        } else {
            JButton applyDebitBtn = createPrimaryButton("Apply for Debit Card");
            applyDebitBtn.addActionListener(e -> {
                String result = loggedInUser.applyDebitCard();
                bank.saveData();
                showSuccess(result);
                setupUserDashboard(loggedInUser); // Refresh dashboard to show card number
                dashboardContentLayout.show(dashboardContent, "Cards");
            });
            debitPanel.add(applyDebitBtn);
        }

        // Credit Card Section
        JPanel creditPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        creditPanel.setBackground(bgColor);
        JLabel creditTitle = new JLabel("Credit Card: ");
        creditTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        creditPanel.add(creditTitle);
        
        if (loggedInUser.getCreditCardNumber() != null) {
            JLabel creditNum = new JLabel(loggedInUser.getCreditCardNumber());
            creditNum.setFont(new Font("Consolas", Font.PLAIN, 20));
            creditPanel.add(creditNum);
        } else {
            JButton applyCreditBtn = createPrimaryButton("Apply for Credit Card");
            applyCreditBtn.addActionListener(e -> {
                String result = loggedInUser.applyCreditCard();
                bank.saveData();
                showSuccess(result);
                setupUserDashboard(loggedInUser); // Refresh dashboard to show card number
                dashboardContentLayout.show(dashboardContent, "Cards");
            });
            creditPanel.add(applyCreditBtn);
        }

        cardsContentPanel.add(debitPanel);
        cardsContentPanel.add(creditPanel);
        
        panel.add(cardsContentPanel, BorderLayout.CENTER);
        return panel;
    }

    // --- UI HELPERS ---

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(textColor);
        return label;
    }

    private JButton createSidebarButton(String text, String panelName) {
        AnimatedButton btn = new AnimatedButton("   " + text, sidebarColor, sidebarHoverColor, 12);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14)); 
        btn.setForeground(new Color(226, 232, 240));
        btn.setMaximumSize(new Dimension(250, 45)); 
        btn.setPreferredSize(new Dimension(250, 45));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        
        if (panelName != null) {
            btn.addActionListener(e -> dashboardContentLayout.show(dashboardContent, panelName));
        }
        return btn;
    }

    private JButton createPrimaryButton(String text) {
        AnimatedButton btn = new AnimatedButton(text, primaryColor, primaryHoverColor, 8);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(220, 45));
        return btn;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    // --- Custom Animated Button Component ---
    class AnimatedButton extends JButton {
        private Color startColor;
        private Color endColor;
        private Color currentColor;
        private Timer timer;
        private float fraction = 0f;
        private boolean isHovering = false;
        private int cornerRadius;

        public AnimatedButton(String text, Color startColor, Color endColor, int radius) {
            super(text);
            this.startColor = startColor;
            this.endColor = endColor;
            this.currentColor = startColor;
            this.cornerRadius = radius;
            
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            timer = new Timer(15, e -> {
                if (isHovering) {
                    fraction += 0.1f;
                    if (fraction >= 1f) {
                        fraction = 1f;
                        timer.stop();
                    }
                } else {
                    fraction -= 0.1f;
                    if (fraction <= 0f) {
                        fraction = 0f;
                        timer.stop();
                    }
                }
                currentColor = blendColors(this.startColor, this.endColor, fraction);
                repaint();
            });

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    isHovering = true;
                    timer.start();
                }
                public void mouseExited(MouseEvent e) {
                    isHovering = false;
                    timer.start();
                }
            });
        }

        private Color blendColors(Color c1, Color c2, float fraction) {
            int r = (int) (c1.getRed() + (c2.getRed() - c1.getRed()) * fraction);
            int g = (int) (c1.getGreen() + (c2.getGreen() - c1.getGreen()) * fraction);
            int b = (int) (c1.getBlue() + (c2.getBlue() - c1.getBlue()) * fraction);
            return new Color(r, g, b);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(currentColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            super.paintComponent(g2);
            g2.dispose();
        }
    }

    // --- Custom Rounded Text Field Component ---
    class RoundedTextField extends JTextField {
        public RoundedTextField() {
            setOpaque(false);
            setBorder(new EmptyBorder(10, 15, 10, 15));
            setFont(new Font("Segoe UI", Font.PLAIN, 15));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
            super.paintComponent(g);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(203, 213, 225)); // Slate 300
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
            g2.dispose();
        }
    }
}
