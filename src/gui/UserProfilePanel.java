package gui;

import db.Database;
import entity.User;
import logic.App;
import logic.Language;
import logic.Session;
import org.hibernate.query.Query;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

public class UserProfilePanel extends JPanel {


    private User user;
    private final boolean isAdmin;
    private boolean isEditing = false;

    private final JTextField nameField;
    private final JTextField usernameField;
    private final JTextField emailField;
    private final JPasswordField passwordField;
    private final JLabel avatarLabel;
    private final JButton changePicButton;
    private final JButton removePicButton;
    private final JButton saveButton;
    private JButton editBackButton;

    private final JPanel passwordPanel;
    private final JComboBox<User> managerComboBox;
    private final JLabel managerLabelDisplay;

    private byte[] selectedPicture;

    Image profile_default = new ImageIcon(getClass().getResource("/profile_default.png")).getImage();
    InputStream profile_default_stream = getClass().getResourceAsStream("/profile_default.png");
    public UserProfilePanel(User user) {
        this.user = user;
        selectedPicture = user.getPicture();
        User currentUser = Session.getInstance().getCurrentUser();
        isAdmin = currentUser != null && "admin".equalsIgnoreCase(currentUser.getRole());
        boolean isSelf = currentUser != null && currentUser.getId().equals(user.getId());


        setLayout(new GridBagLayout());
        setOpaque(false);

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(Color.WHITE);
        box.setPreferredSize(new Dimension(500, 540));
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(30, 30, 30, 30)
        ));

        JLabel title = new JLabel(Language.get("user.profile"), SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(title);
        box.add(Box.createVerticalStrut(20));

        avatarLabel = createAvatarLabel(user);
        box.add(avatarLabel);
        box.add(Box.createVerticalStrut(10));



        nameField = createTextField(user.getName());
        usernameField = createTextField(user.getUsername());
        emailField = createTextField(user.getEmail());
        passwordField = new JPasswordField(user.getPassword());
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        box.add(createFieldPanel(Language.get("user.name")+":", nameField));
        box.add(createFieldPanel(Language.get("user.username")+":", usernameField));
        box.add(createFieldPanel(Language.get("user.email")+":", emailField));

        passwordPanel = createFieldPanel(Language.get("user.password")+":", passwordField);
        box.add(passwordPanel);

        JPanel managerPanel = new JPanel(new BorderLayout(10, 5));
        managerPanel.setOpaque(false);
        JLabel mgrLabel = new JLabel(Language.get("user.manager")+":");
        mgrLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        mgrLabel.setPreferredSize(new Dimension(100, 30));

        managerComboBox = new JComboBox<>();
        managerComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        managerComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        managerLabelDisplay = new JLabel();
        managerLabelDisplay.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        User manager = user.getManager();
        String mgrText = (manager != null) ? manager.getName() + " (" + manager.getUsername() + ")" : Language.get("user.none");
        managerLabelDisplay.setText(mgrText);

        boolean hasPicture;
        try {
            hasPicture = !(user.getPicture() == profile_default_stream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        if (isAdmin) {
            loadManagers();
            managerComboBox.setSelectedItem(manager);
            managerPanel.add(mgrLabel, BorderLayout.WEST);
            managerPanel.add(new JPanel(new CardLayout()) {{
                add(managerComboBox, "edit");
                add(managerLabelDisplay, "view");
            }}, BorderLayout.CENTER);
        } else {
            managerPanel.add(mgrLabel, BorderLayout.WEST);
            managerPanel.add(managerLabelDisplay, BorderLayout.CENTER);
        }

        managerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        box.add(managerPanel);

        box.add(Box.createVerticalStrut(20));

        removePicButton = createButton(Language.get("user.remove_picture"));
        removePicButton.addActionListener(e -> handleRemovePicture(avatarLabel));
        if(hasPicture){
            box.add(removePicButton);
            box.add(Box.createVerticalStrut(10));
        }


        changePicButton = createButton(Language.get("user.change_picture"));
        changePicButton.addActionListener(e -> handleChangePicture(avatarLabel));
        box.add(changePicButton);
        box.add(Box.createVerticalStrut(10));

        saveButton = createButton(Language.get("user.save"));
        saveButton.addActionListener(e -> saveChanges());
        box.add(saveButton);

        if (isSelf || isAdmin) {
            editBackButton = createButton(Language.get("user.edit"));
            editBackButton.addActionListener(e -> toggleEditMode());
            box.add(Box.createVerticalStrut(10));
            box.add(editBackButton);
        }

        disableEditing();
        add(box);
    }



    private void toggleEditMode() {
        isEditing = !isEditing;

        if (isEditing) {
            enableEditing();
            editBackButton.setText(Language.get("user.back"));
        } else {
            disableEditing();
            App.getInstance().setScreen(new UserProfilePanel(user));
        }
    }

    private void enableEditing() {
        nameField.setEditable(true);
        usernameField.setEditable(true);
        emailField.setEditable(true);
        passwordPanel.setVisible(true);
        changePicButton.setVisible(true);
        removePicButton.setVisible(true);
        saveButton.setVisible(true);
        if (isAdmin) {
            managerComboBox.setEnabled(true);
            managerComboBox.setVisible(true);
            managerLabelDisplay.setVisible(false);
        } else {
            managerComboBox.setVisible(false);
            managerLabelDisplay.setVisible(true);
        }
    }

    private void disableEditing() {
        nameField.setEditable(false);
        usernameField.setEditable(false);
        emailField.setEditable(false);
        passwordPanel.setVisible(false);
        changePicButton.setVisible(false);
        removePicButton.setVisible(false);
        saveButton.setVisible(false);
        if (isAdmin) {
            managerComboBox.setEnabled(false);
        }
        managerComboBox.setVisible(false);
        managerLabelDisplay.setVisible(true);
    }

    private void saveChanges() {

        User originalSessionUser = new User();
        originalSessionUser.setId(user.getId());
        originalSessionUser.setName(user.getName());
        originalSessionUser.setUsername(user.getUsername());
        originalSessionUser.setEmail(user.getEmail());
        originalSessionUser.setPassword(user.getPassword());
        originalSessionUser.setRole(user.getRole());
        originalSessionUser.setManager(user.getManager());
        originalSessionUser.setPicture(user.getPicture() != null ? user.getPicture().clone() : null);





        try (org.hibernate.Session session = Database.getSessionFactory().openSession()) {

            user.setName(nameField.getText());
            user.setUsername(usernameField.getText());
            user.setEmail(emailField.getText());
            user.setPassword(new String(passwordField.getPassword()));
            if (isAdmin) user.setManager((User) managerComboBox.getSelectedItem());
            user.setPicture(selectedPicture);

            session.beginTransaction();

            try {
                if(java.util.Arrays.equals(selectedPicture, profile_default_stream.readAllBytes())){
                    user.setPicture(null);


                    session.createNativeQuery("DELETE FROM user_pictures WHERE user_id = :userId")
                            .setParameter("userId", user.getId())
                            .executeUpdate();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }



            session.update(user);
            session.getTransaction().commit();
            logic.NotificationPanel.show(App.getInstance().getLayeredPane(), Language.get("user.success"), 3000,"green");
            App.getInstance().setScreen(new UserProfilePanel(user));
        } catch (Exception ex) {
            user = originalSessionUser;
            if (user.getId().equals(Session.getInstance().getCurrentUser().getId())) Session.getInstance().setCurrentUser(user);
            logic.NotificationPanel.show(App.getInstance().getLayeredPane(), Language.get("user.fail"), 3000,"red");
            App.getInstance().setScreen(new UserProfilePanel(user));
        }
    }

    private void loadManagers() {
        try (org.hibernate.Session session = Database.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE role = 'manager'", User.class);
            List<User> managers = query.list();
            managerComboBox.addItem(null);
            for (User manager : managers) {
                managerComboBox.addItem(manager);
            }
        }
    }

    private JLabel createAvatarLabel(User user) {
        byte[] imageBytes = user.getPicture();
        Image avatarImage;

        if (imageBytes == null || imageBytes.length == 0) {
            avatarImage = profile_default;
        } else {
            try {
                avatarImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
            } catch (IOException e) {
                avatarImage = profile_default;
            }
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
                selectedPicture = Files.readAllBytes(chooser.getSelectedFile().toPath());
                avatarLabel.setIcon(new ImageIcon(img.getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void handleRemovePicture(JLabel avatarLabel) {

        avatarLabel.setIcon(new ImageIcon(profile_default.getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
        try {
            selectedPicture = profile_default_stream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private JTextField createTextField(String text) {
        JTextField field = new JTextField(text);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        return field;
    }

    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setPreferredSize(new Dimension(100, 30));

        panel.add(label, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
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