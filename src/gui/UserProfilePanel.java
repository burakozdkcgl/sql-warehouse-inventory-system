package gui;

import logic.App;
import model.User;

import javax.swing.*;
import java.awt.*;

public class UserProfilePanel extends JPanel {

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Image bg = new ImageIcon(getClass().getResource("/test2.png")).getImage();
        g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
    }

    public UserProfilePanel(User user) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        if (user == null) return;

        // CENTER PANEL CONTAINER
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 20, 20));

        JLabel profilePic = new JLabel(new ImageIcon(user.getProfilePicture().getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH)));
        profilePic.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(profilePic);


        // Spacer
        centerPanel.add(Box.createVerticalStrut(30));

        // Info panel with styled labels
        JLabel usernameLabel = createStyledLabel("Username: " + user.getId());
        JLabel roleLabel = createStyledLabel("Role: " + user.getRole());

        centerPanel.add(usernameLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(roleLabel);

        add(centerPanel, BorderLayout.CENTER);

        // Back button area
        JButton backButton = new JButton("← Back");
        backButton.setFocusPainted(false);
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backButton.setPreferredSize(new Dimension(100, 30));
        backButton.addActionListener(e -> App.getInstance().setScreen(new MainPanel()));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(245, 245, 245));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        bottomPanel.add(backButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }
}
