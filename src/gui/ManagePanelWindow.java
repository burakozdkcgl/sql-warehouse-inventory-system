package gui;

import db.Database;
import entity.Inventory;
import entity.Item;
import entity.User;
import entity.Warehouse;
import logic.App;
import logic.Language;
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
        JLabel title = new JLabel(isCreation ? Language.get("manage.create_user") : "Unknown", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(title);
        box.add(Box.createVerticalStrut(20));

        JTextField nameField = new JTextField("New User");
        JTextField usernameField = new JTextField("newuser");
        JTextField emailField = new JTextField("email@example.com");

        JComboBox<String> roleBox = new JComboBox<>(new String[]{"employee", "manager", "admin", "guest"});

        box.add(createField(Language.get("user.name"), nameField));
        box.add(createField(Language.get("user.username"), usernameField));
        box.add(createField(Language.get("user.email"), emailField));
        box.add(createField(Language.get("user.role"), roleBox));

        JButton createBtn = createButton(Language.get("manage.create_user"));
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
                NotificationPanel.show(App.getInstance().getLayeredPane(), Language.get("manage.nf14"), 3000, "green");
                App.getInstance().setScreen(new ManagePanel());
            } catch (Exception ex) {
                NotificationPanel.show(App.getInstance().getLayeredPane(), Language.get("manage.nf15"), 3000, "red");
            }
        });

        JButton cancelBtn = createButton(Language.get("manage.cancel"));
        cancelBtn.addActionListener(e -> App.getInstance().setScreen(new ManagePanel()));


        box.add(Box.createVerticalStrut(20));

        JLabel line1 = new JLabel(Language.get("manage.create_user_text1"), SwingConstants.CENTER);
        line1.setFont(new Font("Segoe UI", Font.BOLD, 12));
        line1.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(line1);


        JLabel line2 = new JLabel(Language.get("manage.create_user_text2"), SwingConstants.CENTER);
        line2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        line2.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(line2);

        JLabel line3 = new JLabel(Language.get("manage.create_user_text3"), SwingConstants.CENTER);
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
        String message =Language.get("manage.confirm_user_delete");
        String info = user.toString();
        confirmAndDelete(user, User.class, message, info);
    }

    public ManagePanelWindow(Item item) {
        String message = Language.get("manage.confirm_item_delete");
        String info = item.toString();


        confirmAndDelete(item, Item.class, message, info);
    }

    public ManagePanelWindow(Inventory inventory) {
        String message = Language.get("manage.confirm_inventory_delete");
        String info = inventory.toString();
        confirmAndDelete(inventory, Inventory.class, message, info);
    }


    public ManagePanelWindow(Warehouse warehouse) {
        String message = Language.get("manage.confirm_warehouse_delete");
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

        JButton yesBtn = createButton(Language.get("manage.yes_delete"));
        JButton noBtn = createButton(Language.get("manage.cancel"));

        yesBtn.addActionListener(e -> {
            try (org.hibernate.Session session = Database.getSessionFactory().openSession()) {
                session.beginTransaction();

                if (entity instanceof Inventory inv) {
                    Inventory.Pk pk = new Inventory.Pk(inv.getWarehouseId(), inv.getItemId());
                    Inventory attached = session.get(Inventory.class, pk);
                    if (attached != null) session.remove(attached);
                } else {
                    Object attached = session.get(clazz, clazz.getMethod("getId").invoke(entity));
                    if (attached != null) session.remove(attached);
                }

                session.getTransaction().commit();

                // Session logic
                if (entity instanceof User deletedUser &&
                        deletedUser.getId().equals(Session.getInstance().getCurrentUser().getId())) {
                    Session.getInstance().setCurrentUser(null);
                    App.getInstance().setScreen(new LoginPanel());
                } else {
                    App.getInstance().setScreen(new ManagePanel());
                }

                NotificationPanel.show(App.getInstance().getLayeredPane(), Language.get("manage.nf16"), 3000, "green");
            } catch (Exception ex) {
                NotificationPanel.show(App.getInstance().getLayeredPane(), Language.get("manage.nf17"), 3000, "red");
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
