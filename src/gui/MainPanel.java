package gui;

import logic.App;
import model.User;

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

        User user = App.getInstance().getCurrentUser();
        if (user == null) return;

        JPanel whiteBox = new JPanel();
        whiteBox.setLayout(new BorderLayout(10, 10));
        whiteBox.setBackground(Color.WHITE);
        whiteBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(20, 20, 20, 20)
        ));
        whiteBox.setPreferredSize(new Dimension(500, 400));

        // Header with user welcome
        JLabel welcomeLabel = new JLabel("Welcome, " + user.getId(), SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        whiteBox.add(welcomeLabel, BorderLayout.NORTH);

        // Menu panel (left)
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(new EmptyBorder(0, 0, 0, 10));

        String[] menuItems = {"Dashboard", "Profile", "Settings"};
        for (String item : menuItems) {
            JButton menuButton = new JButton(item);
            menuButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            menuPanel.add(menuButton);
            menuPanel.add(Box.createVerticalStrut(10));
            menuButton.addActionListener(e -> {
                App app = App.getInstance();
                switch (item) {
                    case "Profile":
                        app.setScreen(new UserProfilePanel(App.getInstance().getCurrentUser()));
                        break;
                    case "Settings":
                        app.setScreen(new SettingsPanel());
                        break;
                    default:
                        JOptionPane.showMessageDialog(this, item + " is under construction!");
                        break;
                }
            });
            
        }

        whiteBox.add(menuPanel, BorderLayout.WEST);

        // Content placeholder
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(new Color(245, 245, 245));
        contentPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        contentPanel.add(new JLabel("Content Area"));
        whiteBox.add(contentPanel, BorderLayout.CENTER);

        // Admin Button
        if ("admin".equals(user.getRole())) {
            JButton adminButton = new JButton("Open Admin Panel");

            JPanel bottomPanel = new JPanel();
            bottomPanel.setOpaque(false);
            bottomPanel.add(adminButton);
            whiteBox.add(bottomPanel, BorderLayout.SOUTH);
        }

        add(whiteBox);
    }
}
