package gui;

import db.Database;
import entity.User;
import logic.App;
import logic.Session;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;

public class UserProfilePanel extends JPanel {

    private User user;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Image bg = new ImageIcon(getClass().getResource("/test1.png")).getImage();
        g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
    }

    public UserProfilePanel(User user) {
        this.user = user;
        User currentUser = Session.getInstance().getCurrentUser();
        boolean canEdit = currentUser != null && (currentUser.getId().equals(user.getId()) ||
                "admin".equalsIgnoreCase(currentUser.getRole()));

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

        JLabel title = new JLabel("User Profile", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        whiteBox.add(title);
        whiteBox.add(Box.createVerticalStrut(20));

        JLabel avatarLabel = createAvatarLabel(user);
        whiteBox.add(avatarLabel);
        whiteBox.add(Box.createVerticalStrut(10));

        if (canEdit) {
            JButton changePicButton = createButton("Change Picture");
            changePicButton.addActionListener(e -> handleChangePicture(avatarLabel));
            whiteBox.add(changePicButton);
            whiteBox.add(Box.createVerticalStrut(20));
        }

        JTextField usernameField = createTextField(user.getUsername(), canEdit);
        whiteBox.add(createFieldPanel("Username", usernameField));

        JTextField emailField = createTextField(user.getEmail(), canEdit);
        whiteBox.add(createFieldPanel("Email", emailField));

        JPasswordField passwordField = new JPasswordField(user.getPassword());
        passwordField.setEnabled(canEdit);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        whiteBox.add(createFieldPanel("Password", passwordField));

        whiteBox.add(Box.createVerticalStrut(20));

        if (canEdit) {
            JButton saveButton = createButton("Save Changes");
            saveButton.addActionListener(e -> {
                user.setUsername(usernameField.getText());
                user.setEmail(emailField.getText());
                user.setPassword(new String(passwordField.getPassword()));

                try (org.hibernate.Session session = Database.getSessionFactory().openSession()) {
                    session.beginTransaction();
                    session.update(user);
                    session.getTransaction().commit();
                    JOptionPane.showMessageDialog(this, "Profile updated successfully!");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error saving user.");
                }
            });
            whiteBox.add(saveButton);
            whiteBox.add(Box.createVerticalStrut(10));
        }


        add(whiteBox);
    }

    private JLabel createAvatarLabel(User user) {
        byte[] imageBytes = user.getPicture();
        Image avatarImage;
        try {
            avatarImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
        } catch (IOException e) {
            avatarImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        }

        Image scaled = avatarImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        JLabel label = new JLabel(new ImageIcon(scaled));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private void handleChangePicture(JLabel avatarLabel) {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedImage img = ImageIO.read(chooser.getSelectedFile());
                user.setPicture(Files.readAllBytes(chooser.getSelectedFile().toPath()));
                avatarLabel.setIcon(new ImageIcon(img.getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private JTextField createTextField(String text, boolean editable) {
        JTextField field = new JTextField(text);
        field.setEnabled(editable);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        return field;
    }

    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(field);
        panel.add(Box.createVerticalStrut(10));
        return panel;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(new Color(230, 230, 230));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(200, 40));
        return button;
    }
}
