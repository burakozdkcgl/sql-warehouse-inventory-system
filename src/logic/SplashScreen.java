package logic;

import gui.LoginPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SplashScreen extends JPanel {

    private final JProgressBar progressBar;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Image bg = new ImageIcon(getClass().getResource("/splashscreen.png")).getImage();
        g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
    }

    public SplashScreen() {
        setLayout(null);  // Use null layout for manual positioning

        // Create and customize the progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(9, 41, 61));
        progressBar.setBackground(Color.GRAY);
        
        progressBar.setUI(new javax.swing.plaf.basic.BasicProgressBarUI() {
            @Override
            protected Color getSelectionForeground() {
                return Color.WHITE; // Text color over filled area
            }
        
            @Override
            protected Color getSelectionBackground() {
                return Color.WHITE; // Text color over unfilled area
            }
        });

        // Adjust the width and height of the progress bar
        int barWidth = 300;  // Set a reasonable width
        int barHeight = 30;  // Set a reasonable height
        progressBar.setBounds(0, 0, barWidth, barHeight);  // Temporary position

        add(progressBar);  // Add the progress bar to the panel
    }

    @Override
    public void doLayout() {
        super.doLayout();
        // Adjust the final position of the progress bar after the panel's size is set
        int barWidth = 300;  // Set a reasonable width
        int barHeight = 25;  // Set a reasonable height
        progressBar.setBounds((getWidth() - barWidth) / 2, getHeight() - 200, barWidth, barHeight); // Center horizontally, position near the bottom
    }

    public void showSplashScreen(int delay) {
        // Timer to increment progress bar
        Timer progressTimer = getTimer();
        progressTimer.start(); // Start the progress bar animation

        // Timer to change screen after a delay (using java.util.Timer)
        new java.util.Timer().schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                App app = App.getInstance();
                app.setScreen(new LoginPanel()); // Transition to LoginPanel
            }
        }, delay); // The delay before switching to the LoginPanel (e.g., 3 seconds)
    }

    private Timer getTimer() {
        ActionListener progressUpdater = new ActionListener() {
            int progress = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (progress < 100) {
                    progress += 1;  // Increment progress
                    // Ensure that progress bar updates happen on the Event Dispatch Thread (EDT)
                    SwingUtilities.invokeLater(() -> progressBar.setValue(progress));
                } else {
                    ((Timer) e.getSource()).stop(); // Stop the progress timer once progress reaches 100
                }
            }
        };

        return new Timer(30, progressUpdater);
    }
}
