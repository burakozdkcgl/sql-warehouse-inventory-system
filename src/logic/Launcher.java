package logic;

import javax.swing.*;
import java.awt.*;

public class Launcher extends JFrame {
    public Launcher() {
        setTitle("Warehouse Inventory System");
        setSize(300, 150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout());

        // Create the fullscreen checkbox
        JCheckBox fullscreenCheckbox = new JCheckBox("Enable fullscreen");
        fullscreenCheckbox.setSelected(true);  // Set fullscreen as default if needed

        // Create the start button
        JButton startButton = new JButton("Start");

        // Action for the start button
        startButton.addActionListener(e -> {
            boolean isFullscreen = fullscreenCheckbox.isSelected();
            dispose();
            new App(isFullscreen);
        });

        // Add components to the frame
        add(fullscreenCheckbox);
        add(startButton);

        setVisible(true);
    }
}
