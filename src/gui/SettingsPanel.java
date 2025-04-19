package gui;

import logic.App;
import logic.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SettingsPanel extends JPanel {

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Image bg = new ImageIcon(getClass().getResource("/test1.png")).getImage(); // consistent with others
        g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
    }

    public SettingsPanel() {
        setLayout(new GridBagLayout());
        setOpaque(false);

        JPanel whiteBox = new JPanel();
        whiteBox.setLayout(new BoxLayout(whiteBox, BoxLayout.Y_AXIS));
        whiteBox.setBackground(Color.WHITE);
        whiteBox.setPreferredSize(new Dimension(500, 400));
        whiteBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(30, 30, 30, 30)
        ));

        JLabel title = new JLabel("Settings");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(new Color(50, 50, 50));
        whiteBox.add(title);
        whiteBox.add(Box.createVerticalStrut(30));

        JButton fullscreenToggle = createButton("Toggle Fullscreen");
        fullscreenToggle.addActionListener(e -> App.getInstance().toggleFullscreen());
        whiteBox.add(fullscreenToggle);
        whiteBox.add(Box.createVerticalStrut(20));

        JButton myProfileButton = createButton("My Profile");
        myProfileButton.addActionListener(e -> App.getInstance().setScreen(new UserProfilePanel(Session.getInstance().getCurrentUser())));
        whiteBox.add(myProfileButton);
        whiteBox.add(Box.createVerticalStrut(20));

        JButton logoutButton = createButton("Logout");
        logoutButton.addActionListener(e -> {
            Session.getInstance().setCurrentUser(null);
            App.getInstance().setScreen(new LoginPanel());
        });
        whiteBox.add(logoutButton);





        add(whiteBox);


    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(new Color(230, 230, 230));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(200, 40));
        return button;
    }
}
