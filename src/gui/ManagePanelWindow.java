package gui;

import db.Database;
import entity.Item;
import entity.User;
import entity.Warehouse;
import logic.App;
import logic.NotificationPanel;
import logic.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ManagePanelWindow extends JPanel {

    public ManagePanelWindow(User user, boolean isCreation) {
        setLayout(new GridBagLayout());
        setOpaque(false);

        JPanel box = createBoxPanel();
        JLabel title = new JLabel(isCreation ? "Create New User" : "Unknown", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(title);
        box.add(Box.createVerticalStrut(20));

        JTextField nameField = new JTextField("New User");
        JTextField usernameField = new JTextField("newuser");
        JTextField emailField = new JTextField("email@example.com");

        JComboBox<String> roleBox = new JComboBox<>(new String[]{"employee", "manager", "admin"});

        box.add(createField("Name", nameField));
        box.add(createField("Username", usernameField));
        box.add(createField("Email", emailField));
        box.add(createField("Role", roleBox));

        JButton createBtn = createButton("Create");
        createBtn.addActionListener(e -> {
            user.setName(nameField.getText().trim());
            user.setUsername(usernameField.getText().trim());
            user.setEmail(emailField.getText().trim());
            user.setRole((String) roleBox.getSelectedItem());
            user.setPassword("password");

            try (org.hibernate.Session session = Database.getSessionFactory().openSession()) {
                session.beginTransaction();
                session.persist(user);
                session.getTransaction().commit();
                NotificationPanel.show(App.getInstance().getLayeredPane(), "User created", 3000, "green");
                App.getInstance().setScreen(new ManagePanel());
            } catch (Exception ex) {
                NotificationPanel.show(App.getInstance().getLayeredPane(), "Creation failed", 3000, "red");
            }
        });

        JButton cancelBtn = createButton("Cancel");
        cancelBtn.addActionListener(e -> App.getInstance().setScreen(new ManagePanel()));


        box.add(Box.createVerticalStrut(20));

        JLabel line1 = new JLabel("User's password will be set to 'password'", SwingConstants.CENTER);
        line1.setFont(new Font("Segoe UI", Font.BOLD, 12));
        line1.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(line1);


        JLabel line2 = new JLabel("Users can set a new password after their first login from their profile", SwingConstants.CENTER);
        line2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        line2.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(line2);

        JLabel line3 = new JLabel("Admins also can change details by visiting user profiles from user list", SwingConstants.CENTER);
        line3.setFont(new Font("Segoe UI", Font.BOLD, 12));
        line3.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(line3);

        box.add(Box.createVerticalStrut(20));
        box.add(createBtn);
        box.add(Box.createVerticalStrut(10));
        box.add(cancelBtn);
        box.add(Box.createVerticalStrut(30));





        add(box);
    }

    public ManagePanelWindow(User user) {
        String message = "Are you sure you want to delete this user?";
        String info = user.toString();
        confirmAndDelete(user, User.class, message, info);
    }

    public ManagePanelWindow(Item item) {
        String message = "Are you sure you want to delete this item?";
        String info = item.toString();


        confirmAndDelete(item, Item.class, message, info);
    }


    public ManagePanelWindow(Warehouse warehouse) {
        String message = "Are you sure you want to delete this warehouse?";
        String info = warehouse.toString();


        confirmAndDelete(warehouse, Warehouse.class, message, info);
    }


    private void confirmAndDelete(Object entity, Class<?> clazz, String message, String infoText) {
        setLayout(new GridBagLayout());
        setOpaque(false);

        JPanel box = createBoxPanel();

        JLabel title = new JLabel(message, SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(title);
        box.add(Box.createVerticalStrut(15));

        JLabel info = new JLabel(infoText, SwingConstants.CENTER);
        info.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        info.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(info);
        box.add(Box.createVerticalStrut(20));

        JButton yesBtn = createButton("Yes, Delete");
        JButton noBtn = createButton("Cancel");

        yesBtn.addActionListener(e -> {
            try (org.hibernate.Session session = Database.getSessionFactory().openSession()) {
                session.beginTransaction();
                Object attached = session.get(clazz, clazz.getMethod("getId").invoke(entity));
                if (attached != null) session.remove(attached);
                session.getTransaction().commit();

                // Check if the deleted entity is the current user
                if (entity instanceof User deletedUser &&
                        deletedUser.getId().equals(Session.getInstance().getCurrentUser().getId())) {
                    Session.getInstance().setCurrentUser(null); // clear session
                    App.getInstance().setScreen(new LoginPanel()); // redirect to login
                } else {
                    App.getInstance().setScreen(new ManagePanel()); // fallback
                }

                NotificationPanel.show(App.getInstance().getLayeredPane(), "Deleted entity", 3000, "green");
            } catch (Exception ex) {
                NotificationPanel.show(App.getInstance().getLayeredPane(), "Deletion failed", 3000, "red");
            }
        });


        noBtn.addActionListener(e -> App.getInstance().setScreen(new ManagePanel()));

        box.add(yesBtn);
        box.add(Box.createVerticalStrut(10));
        box.add(noBtn);
        add(box);
    }

    private JPanel createBoxPanel() {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(Color.WHITE);
        box.setPreferredSize(new Dimension(500, 400));
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(30, 30, 30, 30)
        ));
        return box;
    }

    private JPanel createField(String labelText, JComponent input) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setPreferredSize(new Dimension(100, 30));
        panel.add(label, BorderLayout.WEST);
        panel.add(input, BorderLayout.CENTER);
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
