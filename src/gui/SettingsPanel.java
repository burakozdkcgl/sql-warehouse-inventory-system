package gui;

import logic.App;
import logic.Language;
import logic.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SettingsPanel extends JPanel {



    public SettingsPanel() {
        setLayout(new GridBagLayout());
        setOpaque(false);

        JPanel whiteBox = new JPanel();
        whiteBox.setLayout(new BoxLayout(whiteBox, BoxLayout.Y_AXIS));
        whiteBox.setBackground(Color.WHITE);
        whiteBox.setPreferredSize(new Dimension(500, 540));
        whiteBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(30, 30, 30, 30)
        ));

        JLabel title = new JLabel(Language.get("settings.title"));
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(new Color(50, 50, 50));
        whiteBox.add(title);
        whiteBox.add(Box.createVerticalStrut(30));

        JButton fullscreenToggle = createButton(Language.get("settings.toggle"));
        fullscreenToggle.addActionListener(e -> App.getInstance().toggleFullscreen());
        whiteBox.add(fullscreenToggle);
        whiteBox.add(Box.createVerticalStrut(20));

        JButton languageToggle = createButton(Language.get("launcher.language"));
        languageToggle.addActionListener(e -> {
            String current = Session.getInstance().getLanguage();
            String newLang = current.equals("en") ? "tr" : "en";
            Session.getInstance().setLanguage(newLang);  // will trigger Language.load()

            // refresh UI with new panels
            App.getInstance().setScreen(new SettingsPanel());
        });
        whiteBox.add(languageToggle);
        whiteBox.add(Box.createVerticalStrut(20));



        JButton myProfileButton = createButton(Language.get("settings.my_profile"));
        myProfileButton.addActionListener(e -> App.getInstance().setScreen(new UserProfilePanel(Session.getInstance().getCurrentUser())));
        whiteBox.add(myProfileButton);
        whiteBox.add(Box.createVerticalStrut(20));

        JButton logoutButton = createButton(Language.get("settings.logout"));
        logoutButton.addActionListener(e -> {
            Session.getInstance().setCurrentUser(null);
            App.getInstance().setScreen(new LoginPanel());
        });
        whiteBox.add(logoutButton);
        whiteBox.add(Box.createVerticalStrut(20));

        JButton exitButton = createButton(Language.get("settings.exit"));
        exitButton.addActionListener(e -> {
            try {
                db.Database.close();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            System.exit(0);
        });
        whiteBox.add(exitButton);



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
