package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import entity.User;
import logic.App;
import db.Auth;
import logic.NotificationPanel;
import logic.Session;

public class LoginPanel extends JPanel {

    public LoginPanel() {

        setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;

        // Top spacer (more weight = pushes form downward less)
        gbc.gridy = 0;
        gbc.weighty = 12;
        centerPanel.add(Box.createVerticalGlue(), gbc);

        // Form panel
        gbc.gridy = 1;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        centerPanel.add(getFormPanel(), gbc);

        // Bottom spacer
        gbc.gridy = 2;
        gbc.weighty = 1;
        centerPanel.add(Box.createVerticalGlue(), gbc);

        add(centerPanel, BorderLayout.CENTER);


    }


    private JPanel getFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 240));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };

        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.anchor = GridBagConstraints.WEST;

        // Username and Password Fields
        formPanel.add(createLabel("Username:"), gbc);
        gbc.gridx = 1;
        JTextField idField = new JTextField(12);
        formPanel.add(idField, gbc);

        gbc.gridx = 2;
        formPanel.add(createLabel("Password:"), gbc);
        gbc.gridx = 3;
        JPasswordField passField = new JPasswordField(12);
        formPanel.add(passField, gbc);

        // Login Button
        gbc.gridx = 4;
        JButton loginButton = new JButton("Login");
        styleButton(loginButton);
        formPanel.add(loginButton, gbc);

        loginButton.addActionListener((ActionEvent e) -> {
            String username = idField.getText();
            String password = new String(passField.getPassword());
            User user = Auth.attemptLogin(username, password);
            if (user != null) {
                Session.getInstance().setCurrentUser(user);
                App.getInstance().setScreen(new MainPanel());
            } else {
                NotificationPanel.show(App.getInstance().getLayeredPane(), "Invalid credentials!", 3000);

            }

        });

        return formPanel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(Color.DARK_GRAY);
        return label;
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Image bg = new ImageIcon(getClass().getResource("/background_login.png")).getImage();
        g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
    }
}
