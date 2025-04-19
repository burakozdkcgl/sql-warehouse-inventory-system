package gui;

import db.Database;
import logic.App;
import entity.User;
import org.hibernate.Session;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;

public class UserProfilePanel extends JPanel {

    private User user;

    public UserProfilePanel(User user) {
        this.user = user;
        setLayout(new BorderLayout());

        // Custom background image
        setOpaque(false);

        // MAIN BOX BACKGROUND
        JPanel contentBox = new JPanel();
        contentBox.setLayout(new BoxLayout(contentBox, BoxLayout.Y_AXIS));
        contentBox.setBackground(new Color(255, 255, 255, 220)); // translucent white
        contentBox.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        contentBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (user == null) return;

        // Avatar + Change button
        JLabel avatarLabel = createAvatarLabel(user);
        JButton changePicButton = new JButton("Change Picture");
        changePicButton.addActionListener(e -> handleChangePicture(avatarLabel));
        changePicButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentBox.add(Box.createVerticalStrut(10));
        contentBox.add(avatarLabel);
        contentBox.add(Box.createVerticalStrut(5));
        contentBox.add(changePicButton);
        contentBox.add(Box.createVerticalStrut(20));

        // Username
        JTextField usernameField = createTextField(user.getUsername());
        contentBox.add(createFieldPanel("Username", usernameField));

        // Email
        JTextField emailField = createTextField(user.getEmail());
        contentBox.add(createFieldPanel("Email", emailField));

        // Password
        JPasswordField passwordField = new JPasswordField(user.getPassword());
        contentBox.add(createFieldPanel("Password", passwordField));

        // Manager
        if (user.getManager() != null) {
            JLabel managerLabel = createStyledLabel("Manager: " + user.getManager().getName());
            contentBox.add(Box.createVerticalStrut(10));
            contentBox.add(managerLabel);
        }

        contentBox.add(Box.createVerticalStrut(20));

        // Save button
        JButton saveButton = new JButton("Save Changes");
        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveButton.addActionListener(e -> {
            user.setUsername(usernameField.getText());
            user.setEmail(emailField.getText());
            user.setPassword(new String(passwordField.getPassword()));

            // Hibernate session update
            try (Session session = Database.getSessionFactory().openSession()) {
                session.beginTransaction();
                session.update(user); // or session.merge(user) if it's detached
                session.getTransaction().commit();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving user.");
                return;
            }

            JOptionPane.showMessageDialog(this, "Profile updated successfully!");
        });


        contentBox.add(saveButton);

        // Back button
        JButton backButton = new JButton("← Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> App.getInstance().setScreen(new MainPanel()));

        contentBox.add(Box.createVerticalStrut(10));
        contentBox.add(backButton);

        add(contentBox, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Image bg = new ImageIcon(getClass().getResource("/test2.png")).getImage();
        g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
    }

    private JLabel createAvatarLabel(User user) {
        byte[] imageBytes = user.getPicture();

        Image avatarImage;
        try {
            avatarImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
        } catch (IOException e) {
            avatarImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        }

        Image scaledAvatar = avatarImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        JLabel avatarLabel = new JLabel(new ImageIcon(scaledAvatar));
        avatarLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return avatarLabel;
    }

    private void handleChangePicture(JLabel avatarLabel) {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedImage newImage = ImageIO.read(chooser.getSelectedFile());
                ImageIcon newIcon = new ImageIcon(newImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH));
                avatarLabel.setIcon(newIcon);
                user.setPicture(Files.readAllBytes(chooser.getSelectedFile().toPath()));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel label = createStyledLabel(labelText);
        panel.add(label);
        panel.add(field);
        panel.add(Box.createVerticalStrut(10));
        return panel;
    }

    private JTextField createTextField(String text) {
        JTextField field = new JTextField(text);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return field;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
}
