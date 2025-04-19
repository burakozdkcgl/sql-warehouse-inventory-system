package gui;

import logic.App;
import logic.Session;
import entity.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainPanel extends JPanel {

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Image bg = new ImageIcon(getClass().getResource("/test1.png")).getImage();
        g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
    }

    public MainPanel() {
        setLayout(new GridBagLayout());
        setOpaque(false);

        User user = Session.getInstance().getCurrentUser();
        if (user == null) return;

        JPanel whiteBox = new JPanel();
        whiteBox.setLayout(new BorderLayout(10, 10));
        whiteBox.setBackground(Color.WHITE);
        whiteBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(20, 20, 20, 20)
        ));
        whiteBox.setPreferredSize(new Dimension(950, 620));

        // Header with user welcome
        JLabel welcomeLabel = new JLabel("Welcome, " + user.getId(), SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        whiteBox.add(welcomeLabel, BorderLayout.NORTH);

        // Menu panel (left)
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(new EmptyBorder(0, 0, 0, 10));


        JButton itemListButton = new JButton("View Items");
        itemListButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        itemListButton.addActionListener(e -> App.getInstance().setScreen(new ItemListPanel()));
        menuPanel.add(itemListButton);
        menuPanel.add(Box.createVerticalStrut(10));

        JButton inventoryButton = new JButton("View Inventory");
        inventoryButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        inventoryButton.addActionListener(e -> {
            App.getInstance().setScreen(new InventoryPanel());
        });
        menuPanel.add(inventoryButton);
        menuPanel.add(Box.createVerticalStrut(10));

        JButton userListButton = new JButton("View Users");
        userListButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        userListButton.addActionListener(e -> {
            App.getInstance().setScreen(new UserListPanel());
        });
        menuPanel.add(userListButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.addActionListener(e -> {
            Session.getInstance().setCurrentUser(null); // Clear the session
            App.getInstance().setScreen(new LoginPanel()); // Return to login screen
        });
        menuPanel.add(Box.createVerticalStrut(20)); // Extra space before logout
        menuPanel.add(logoutButton);


        whiteBox.add(menuPanel, BorderLayout.WEST);

        // Content placeholder
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(new Color(245, 245, 245));
        contentPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        contentPanel.add(new JLabel("Content Area"));
        whiteBox.add(contentPanel, BorderLayout.CENTER);


        add(whiteBox);
    }
}
