package gui;

import logic.App;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Image bg = new ImageIcon(getClass().getResource("/test2.png")).getImage();
        g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
    }

    public SettingsPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel label = new JLabel("Settings");
        label.setFont(new Font("Arial", Font.BOLD, 20));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(label);

        add(Box.createVerticalStrut(20));

        JButton fullscreenToggle = new JButton("Toggle Fullscreen");
        fullscreenToggle.setAlignmentX(Component.CENTER_ALIGNMENT);
        fullscreenToggle.addActionListener(e -> App.getInstance().toggleFullscreen());
        add(fullscreenToggle);

        add(Box.createVerticalStrut(20));
        JButton backButton = new JButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> App.getInstance().setScreen(new MainPanel()));
        add(backButton);

    }
}
