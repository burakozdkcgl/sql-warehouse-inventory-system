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
import java.util.List;
import java.util.Objects;

public class
ManagePanel extends JPanel {

    public ManagePanel() {
        setLayout(new GridBagLayout());
        setOpaque(false);

        JPanel whiteBox = new JPanel(new BorderLayout(10, 10));
        whiteBox.setBackground(Color.WHITE);
        whiteBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(20, 20, 20, 20)
        ));
        whiteBox.setPreferredSize(new Dimension(950, 620));

        JLabel title;
        if (Objects.equals(Session.getInstance().getCurrentUser().getRole(), "admin"))
            title = new JLabel(Language.get("manage.admintitle"), SwingConstants.CENTER);
        else
            title = new JLabel(Language.get("manage.managertitle"), SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        whiteBox.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(Box.createVerticalStrut(15));
        content.add(createSectionPanel(Language.get("manage.set_reorder_levels"), createReorderForm()));
        content.add(Box.createVerticalStrut(15));
        content.add(createSectionPanel(Language.get("manage.manage_users"), createUserManagementPanel()));
        content.add(Box.createVerticalStrut(15));
        content.add(createSectionPanel(Language.get("manage.manage_items"), createItemManagementPanel()));
        content.add(Box.createVerticalStrut(15));
        content.add(createSectionPanel(Language.get("manage.delete_inventory_records"), createInventoryManagementPanel()));
        content.add(Box.createVerticalStrut(15));
        content.add(createSectionPanel(Language.get("manage.manage_warehouses"), createWarehouseManagementPanel()));
        content.add(Box.createVerticalStrut(15));



        JPanel contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));
        contentWrapper.setOpaque(false);
        contentWrapper.add(content);

        JScrollPane scrollPane = new JScrollPane(contentWrapper,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        whiteBox.add(scrollPane, BorderLayout.CENTER);
        whiteBox.add(Box.createVerticalStrut(20), BorderLayout.SOUTH);
        add(whiteBox);
    }

    private JPanel createSectionPanel(String titleText, JComponent contentComponent) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(Color.WHITE);
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(15, 15, 15, 15)
        ));
        section.setAlignmentX(Component.LEFT_ALIGNMENT); // allow flexible layout

        JPanel contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));
        contentWrapper.setOpaque(false);
        contentWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);
        titlePanel.add(title);

        contentWrapper.add(titlePanel);
        contentWrapper.add(Box.createVerticalStrut(10));
        contentWrapper.add(contentComponent);

        section.add(contentWrapper);

        // Let section grow freely
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        section.setPreferredSize(new Dimension(900, section.getPreferredSize().height));

        return section;
    }





    private JPanel createReorderForm() {
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        form.setOpaque(false);

        JComboBox<Warehouse> warehouseBox = new JComboBox<>();
        JComboBox<Item> itemBox = new JComboBox<>();
        JSpinner reorderSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 1000000000, 1));
        JButton saveBtn = createButton(Language.get("manage.save"));

        warehouseBox.setPreferredSize(new Dimension(200, 25));
        itemBox.setPreferredSize(new Dimension(200, 25));
        reorderSpinner.setPreferredSize(new Dimension(80, 25));

        form.add(new JLabel(Language.get("warehouse.name")+":"));
        form.add(warehouseBox);
        form.add(new JLabel(Language.get("item.name")+":"));
        form.add(itemBox);
        form.add(new JLabel(Language.get("item.reorder")+":"));
        form.add(reorderSpinner);
        form.add(saveBtn);

        try (org.hibernate.Session session = Database.getSessionFactory().openSession()) {
            List<Warehouse> warehouses = session.createQuery("FROM Warehouse", Warehouse.class).list();
            for (Warehouse w : warehouses) warehouseBox.addItem(w);
        }

        warehouseBox.addActionListener(e -> {
            itemBox.removeAllItems();
            Warehouse selected = (Warehouse) warehouseBox.getSelectedItem();
            if (selected == null) return;

            try (org.hibernate.Session session = Database.getSessionFactory().openSession()) {
                List<Inventory> inventoryList = session.createQuery(
                                "FROM Inventory WHERE warehouse_id = :wid", Inventory.class)
                        .setParameter("wid", selected.getId())
                        .list();

                for (Inventory inv : inventoryList) {
                    Item item = session.get(Item.class, inv.getItemId());
                    if (item != null) itemBox.addItem(item);
                }
            }
        });

        itemBox.addActionListener(e -> {
            Warehouse warehouse = (Warehouse) warehouseBox.getSelectedItem();
            Item item = (Item) itemBox.getSelectedItem();
            if (warehouse == null || item == null) return;

            try (org.hibernate.Session session = Database.getSessionFactory().openSession()) {
                Inventory inv = session.get(Inventory.class,
                        new Inventory.Pk(warehouse.getId(), item.getId()));
                reorderSpinner.setValue(inv != null ? inv.getReorderLevel() : 0);
            }
        });

        saveBtn.addActionListener(e -> {
            Warehouse warehouse = (Warehouse) warehouseBox.getSelectedItem();
            Item item = (Item) itemBox.getSelectedItem();
            if (warehouse == null || item == null) return;

            try (org.hibernate.Session session = Database.getSessionFactory().openSession()) {
                session.beginTransaction();
                Inventory inv = session.get(Inventory.class,
                        new Inventory.Pk(warehouse.getId(), item.getId()));
                if (inv != null) {
                    inv.setReorderLevel((Integer) reorderSpinner.getValue());
                    session.merge(inv);
                    session.getTransaction().commit();
                    NotificationPanel.show(App.getInstance().getLayeredPane(), Language.get("manage.nf1"), 3000, "green");
                } else {
                    NotificationPanel.show(App.getInstance().getLayeredPane(), Language.get("manage.nf2"), 3000, "red");
                }
            }
        });

        if (warehouseBox.getItemCount() > 0) warehouseBox.setSelectedIndex(0);

        return form;
    }

    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        // Main Buttons
        JButton createUserBtn = createButton(Language.get("manage.create_user"));
        JButton removeUserBtn = createButton(Language.get("manage.delete_user"));
        JButton promoteUserBtn = createButton(Language.get("manage.promote_user"));
        JButton depromoteUserBtn = createButton(Language.get("manage.demote_user"));
        JButton assignManagerBtn = createButton(Language.get("manage.assign_manager"));


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.add(createUserBtn);
        buttonPanel.add(removeUserBtn);
        buttonPanel.add(promoteUserBtn);
        buttonPanel.add(depromoteUserBtn);
        buttonPanel.add(assignManagerBtn);
        panel.add(buttonPanel);

        // Operation Panel
        JPanel operationPanel = new JPanel(new BorderLayout());
        operationPanel.setOpaque(false);
        operationPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        operationPanel.setPreferredSize(new Dimension(900, 250));

        createUserBtn.addActionListener(e -> {
            User newUser = new User();
            App.getInstance().setScreen(new ManagePanelWindow(newUser, true));
        });

        // Remove/Promote/Depromote actions
        removeUserBtn.addActionListener(e -> {
            panel.remove(buttonPanel);
            operationPanel.removeAll();
            operationPanel.add(createRemoveUserPanel(panel, operationPanel, buttonPanel), BorderLayout.CENTER);
            panel.add(operationPanel);
            panel.revalidate();
            panel.repaint();
        });

        promoteUserBtn.addActionListener(e -> {
            panel.remove(buttonPanel);
            operationPanel.removeAll();
            operationPanel.add(createPromoteUserPanel(panel, operationPanel, buttonPanel, true), BorderLayout.CENTER);
            panel.add(operationPanel);
            panel.revalidate();
            panel.repaint();
        });

        depromoteUserBtn.addActionListener(e -> {
            panel.remove(buttonPanel);
            operationPanel.removeAll();
            operationPanel.add(createPromoteUserPanel(panel, operationPanel, buttonPanel, false), BorderLayout.CENTER);
            panel.add(operationPanel);
            panel.revalidate();
            panel.repaint();
        });

        assignManagerBtn.addActionListener(e -> {
            panel.remove(buttonPanel);
            operationPanel.removeAll();
            operationPanel.add(createAssignManagerPanel(panel, operationPanel, buttonPanel), BorderLayout.CENTER);
            panel.add(operationPanel);
            panel.revalidate();
            panel.repaint();
        });


        return panel;
    }

    private JPanel createRemoveUserPanel(JPanel root, JPanel operationPanel, JPanel buttonPanel) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setOpaque(false);

        JComboBox<User> userBox = new JComboBox<>();
        JButton deleteBtn = createButton(Language.get("manage.delete"));
        JButton backBtn = createButton(Language.get("manage.back"));

        try (org.hibernate.Session session = Database.getSessionFactory().openSession()) {
            session.createQuery("FROM User", User.class).list().forEach(userBox::addItem);
        }

        panel.add(userBox);
        panel.add(deleteBtn);
        panel.add(backBtn);

        deleteBtn.addActionListener(e -> {
            User selected = (User) userBox.getSelectedItem();
            if (selected == null) return;
            App.getInstance().setScreen(new ManagePanelWindow(selected));
        });

        backBtn.addActionListener(e -> {
            root.remove(operationPanel);
            root.add(buttonPanel);
            root.revalidate();
            root.repaint();
        });

        return panel;
    }

    private JPanel createPromoteUserPanel(JPanel root, JPanel operationPanel, JPanel buttonPanel, boolean promote) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setOpaque(false);

        JComboBox<User> userBox = new JComboBox<>();
        JButton confirmBtn = createButton(promote ? Language.get("manage.promote_user") : Language.get("manage.demote_user"));
        JButton backBtn = createButton(Language.get("manage.back"));

        try (org.hibernate.Session session = Database.getSessionFactory().openSession()) {
            session.createQuery("FROM User", User.class).list().forEach(user -> {
                if (promote && "employee".equals(user.getRole())) userBox.addItem(user);
                else if (!promote && "manager".equals(user.getRole())) userBox.addItem(user);
            });
        }

        panel.add(userBox);
        panel.add(confirmBtn);
        panel.add(backBtn);

        confirmBtn.addActionListener(e -> {
            User selected = (User) userBox.getSelectedItem();
            if (selected == null) return;

            try (org.hibernate.Session session = Database.getSessionFactory().openSession()) {
                session.beginTransaction();

                if (!promote) {
                    // Depromoting: clear manager links
                    List<User> managedUsers = session.createQuery(
                                    "FROM User WHERE manager.id = :managerId", User.class)
                            .setParameter("managerId", selected.getId())
                            .list();
                    for (User u : managedUsers) {
                        u.setManager(null);
                        session.merge(u);
                    }
                }

                selected.setRole(promote ? "manager" : "employee");
                session.merge(selected);
                session.getTransaction().commit();

                NotificationPanel.show(App.getInstance().getLayeredPane(),
                        Language.get(promote ? "manage.nf3" : "manage.nf4"), 3000, "green");
                if (Objects.equals(selected.getId(), Session.getInstance().getCurrentUser().getId())) {
                    Session.getInstance().setCurrentUser(selected); // refresh session user
                    App.getInstance().setScreen(new MainPanel()); // re-invoke with updated role
                } else {
                    userBox.removeItem(selected);
                }
            }
        });


        backBtn.addActionListener(e -> {
            root.remove(operationPanel);
            root.add(buttonPanel);
            root.revalidate();
            root.repaint();
        });

        return panel;
    }




    private JPanel createAssignManagerPanel(JPanel root, JPanel operationPanel, JPanel buttonPanel) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setOpaque(false);

        JComboBox<User> employeeBox = new JComboBox<>();
        JComboBox<User> managerBox = new JComboBox<>();
        JButton assignBtn = createButton(Language.get("manage.save"));
        JButton backBtn = createButton(Language.get("manage.back"));

        managerBox.addItem(null); // Blank option

        try (org.hibernate.Session session = Database.getSessionFactory().openSession()) {
            session.createQuery("FROM User", User.class).list().forEach(user -> {
                if ("employee".equals(user.getRole())) employeeBox.addItem(user);
                if ("manager".equals(user.getRole())) managerBox.addItem(user);
            });
        }

        panel.add(new JLabel(Language.get("user.employee")+":"));
        panel.add(employeeBox);
        panel.add(new JLabel(Language.get("user.manager")+":"));
        panel.add(managerBox);
        panel.add(assignBtn);
        panel.add(backBtn);

        assignBtn.addActionListener(e -> {
            User employee = (User) employeeBox.getSelectedItem();
            User manager = (User) managerBox.getSelectedItem();
            if (employee == null) return;

            try (org.hibernate.Session session = Database.getSessionFactory().openSession()) {
                session.beginTransaction();
                employee.setManager(manager);
                session.merge(employee);
                session.getTransaction().commit();
                NotificationPanel.show(App.getInstance().getLayeredPane(), Language.get("manage.nf5"), 3000, "green");
            } catch (Exception ex) {
                NotificationPanel.show(App.getInstance().getLayeredPane(), Language.get("manage.nf6"), 3000, "red");
            }
        });

        backBtn.addActionListener(e -> {
            root.remove(operationPanel);
            root.add(buttonPanel);
            root.revalidate();
            root.repaint();
        });

        return panel;
    }









    private JPanel createItemManagementPanel() {
        JPanel container = new JPanel(new BorderLayout(15, 0));
        container.setOpaque(false);

        DefaultListModel<Item> itemListModel = new DefaultListModel<>();
        JList<Item> itemJList = new JList<>(itemListModel);
        itemJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemJList.setVisibleRowCount(10);
        itemJList.setFixedCellHeight(30);
        itemJList.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        try (org.hibernate.Session session = Database.getSessionFactory().openSession()) {
            session.createQuery("FROM Item", Item.class).list().forEach(itemListModel::addElement);
        }

        Item placeholder = new Item() {
            @Override
            public String toString() {
                return Language.get("manage.add_new");
            }
        };
        itemListModel.addElement(placeholder);

        JScrollPane listScroll = new JScrollPane(itemJList);
        listScroll.setPreferredSize(new Dimension(250, 200));
        container.add(listScroll, BorderLayout.WEST);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createTitledBorder(Language.get("manage.item_details")));

        JLabel modeLabel = new JLabel();
        modeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        modeLabel.setForeground(Color.DARK_GRAY);

        JTextField nameField = new JTextField(20);
        JTextField skuField = new JTextField(20);
        JTextField categoryField = new JTextField(20);
        JTextField descriptionField = new JTextField(20);

        JButton saveBtn = createButton(Language.get("manage.save"));
        JButton deleteBtn = createButton(Language.get("manage.delete"));
        JButton cancelBtn = createButton(Language.get("manage.cancel"));

        final Item[] currentEdit = {null};

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        formPanel.add(modeLabel, gbc); row++;

        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel(Language.get("item.name")+":"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel(Language.get("item.sku")+":"), gbc);
        gbc.gridx = 1;
        formPanel.add(skuField, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel(Language.get("item.category")+":"), gbc);
        gbc.gridx = 1;
        formPanel.add(categoryField, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel(Language.get("item.description")+":"), gbc);
        gbc.gridx = 1;
        formPanel.add(descriptionField, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setOpaque(false);
        btnRow.add(saveBtn);
        btnRow.add(deleteBtn);
        btnRow.add(cancelBtn);
        formPanel.add(btnRow, gbc);

        container.add(formPanel, BorderLayout.CENTER);

        Runnable clearForm = () -> {
            currentEdit[0] = null;
            nameField.setText("");
            skuField.setText("");
            categoryField.setText("");
            descriptionField.setText("");
            modeLabel.setText(Language.get("manage.create_item"));
        };

        itemJList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Item selected = itemJList.getSelectedValue();
                if (selected == null) return;
                if (selected.toString().equals(Language.get("manage.add_new"))) {
                    itemJList.clearSelection();
                    clearForm.run();
                    return;
                }
                currentEdit[0] = selected;
                nameField.setText(selected.getName());
                skuField.setText(selected.getSku());
                categoryField.setText(selected.getCategory());
                descriptionField.setText(selected.getDescription());
                modeLabel.setText(Language.get("manage.editing")+": " + selected.getName());
            }
        });

        cancelBtn.addActionListener(e -> {
            itemJList.clearSelection();
            clearForm.run();
        });

        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String sku = skuField.getText().trim();
            String category = categoryField.getText().trim();
            String desc = descriptionField.getText().trim();

            if (name.isEmpty() || sku.isEmpty()) {
                NotificationPanel.show(App.getInstance().getLayeredPane(), Language.get("manage.nf7"), 3000, "red");
                return;
            }

            try (org.hibernate.Session session = Database.getSessionFactory().openSession()) {
                session.beginTransaction();
                if (currentEdit[0] == null) {
                    Item item = new Item();
                    item.setName(name);
                    item.setSku(sku);
                    item.setCategory(category);
                    item.setDescription(desc);
                    session.persist(item);
                    itemListModel.insertElementAt(item, itemListModel.getSize() - 1);
                    NotificationPanel.show(App.getInstance().getLayeredPane(), Language.get("manage.nf8"), 3000, "green");
                } else {
                    currentEdit[0].setName(name);
                    currentEdit[0].setSku(sku);
                    currentEdit[0].setCategory(category);
                    currentEdit[0].setDescription(desc);
                    session.merge(currentEdit[0]);
                    itemJList.repaint();
                    NotificationPanel.show(App.getInstance().getLayeredPane(), Language.get("manage.nf9"), 3000, "green");
                }
                session.getTransaction().commit();
            } catch (Exception ex) {
                NotificationPanel.show(App.getInstance().getLayeredPane(), Language.get("manage.nf10"), 3000, "red");
            }
            clearForm.run();
        });

        deleteBtn.addActionListener(e -> {

            Item selected = itemJList.getSelectedValue();
            if (selected == null || selected.toString().equals(Language.get("manage.add_new"))) return;
            App.getInstance().setScreen(new ManagePanelWindow(selected));

        });

        clearForm.run();
        return container;
    }



    private JPanel createWarehouseManagementPanel() {
        JPanel container = new JPanel(new BorderLayout(15, 0));
        container.setOpaque(false);

        DefaultListModel<Warehouse> warehouseListModel = new DefaultListModel<>();
        JList<Warehouse> warehouseJList = new JList<>(warehouseListModel);
        warehouseJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        warehouseJList.setVisibleRowCount(10);
        warehouseJList.setFixedCellHeight(30);
        warehouseJList.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        try (org.hibernate.Session session = Database.getSessionFactory().openSession()) {
            session.createQuery("FROM Warehouse", Warehouse.class).list().forEach(warehouseListModel::addElement);
        }

        Warehouse placeholder = new Warehouse() {
            @Override
            public String toString() {
                return Language.get("manage.add_new");
            }
        };
        warehouseListModel.addElement(placeholder);

        JScrollPane listScroll = new JScrollPane(warehouseJList);
        listScroll.setPreferredSize(new Dimension(250, 200));
        container.add(listScroll, BorderLayout.WEST);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createTitledBorder(Language.get("manage.warehouse_details")));

        JLabel modeLabel = new JLabel();
        modeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        modeLabel.setForeground(Color.DARK_GRAY);

        JTextField nameField = new JTextField(20);
        JTextField locationField = new JTextField(20);
        JTextField descriptionField = new JTextField(20);

        JButton saveBtn = createButton(Language.get("manage.save"));
        JButton deleteBtn = createButton(Language.get("manage.delete"));
        JButton cancelBtn = createButton(Language.get("manage.cancel"));

        final Warehouse[] currentEdit = {null};

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        formPanel.add(modeLabel, gbc); row++;

        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel(Language.get("warehouse.name")+":"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel(Language.get("warehouse.location")+":"), gbc);
        gbc.gridx = 1;
        formPanel.add(locationField, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel(Language.get("warehouse.description")+":"), gbc);
        gbc.gridx = 1;
        formPanel.add(descriptionField, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setOpaque(false);
        btnRow.add(saveBtn);
        btnRow.add(deleteBtn);
        btnRow.add(cancelBtn);
        formPanel.add(btnRow, gbc);

        container.add(formPanel, BorderLayout.CENTER);

        Runnable clearForm = () -> {
            currentEdit[0] = null;
            nameField.setText("");
            locationField.setText("");
            descriptionField.setText("");
            modeLabel.setText(Language.get("manage.create_warehouse"));
        };

        warehouseJList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Warehouse selected = warehouseJList.getSelectedValue();
                if (selected == null) return;
                if (selected.toString().equals(Language.get("manage.add_new"))) {
                    warehouseJList.clearSelection();
                    clearForm.run();
                    return;
                }
                currentEdit[0] = selected;
                nameField.setText(selected.getName());
                locationField.setText(selected.getLocation());
                descriptionField.setText(selected.getDescription());
                modeLabel.setText(Language.get("manage.editing")+": " + selected.getName());
            }
        });

        cancelBtn.addActionListener(e -> {
            warehouseJList.clearSelection();
            clearForm.run();
        });

        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String loc = locationField.getText().trim();
            String desc = descriptionField.getText().trim();

            if (name.isEmpty()) {
                NotificationPanel.show(App.getInstance().getLayeredPane(), Language.get("manage.nf11"), 3000, "red");
                return;
            }

            try (org.hibernate.Session session = Database.getSessionFactory().openSession()) {
                session.beginTransaction();
                if (currentEdit[0] == null) {
                    Warehouse w = new Warehouse();
                    w.setName(name);
                    w.setLocation(loc);
                    w.setDescription(desc);
                    session.persist(w);
                    warehouseListModel.insertElementAt(w, warehouseListModel.getSize() - 1);
                    NotificationPanel.show(App.getInstance().getLayeredPane(), Language.get("manage.nf12"), 3000, "green");
                } else {
                    currentEdit[0].setName(name);
                    currentEdit[0].setLocation(loc);
                    currentEdit[0].setDescription(desc);
                    session.merge(currentEdit[0]);
                    warehouseJList.repaint();
                    NotificationPanel.show(App.getInstance().getLayeredPane(), Language.get("manage.nf13"), 3000, "green");
                }
                session.getTransaction().commit();
            } catch (Exception ex) {
                NotificationPanel.show(App.getInstance().getLayeredPane(), Language.get("manage.nf10"), 3000, "red");
            }
            clearForm.run();
        });

        deleteBtn.addActionListener(e -> {
            Warehouse selected = warehouseJList.getSelectedValue();
            if (selected == null || selected.toString().equals(Language.get("manage.add_new"))) return;
            App.getInstance().setScreen(new ManagePanelWindow(selected));
        });

        clearForm.run();
        return container;
    }


    private JPanel createInventoryManagementPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setOpaque(false);

        JComboBox<Warehouse> warehouseBox = new JComboBox<>();
        JComboBox<Item> itemBox = new JComboBox<>();
        JButton removeBtn = createButton(Language.get("manage.remove"));

        warehouseBox.setPreferredSize(new Dimension(200, 25));
        itemBox.setPreferredSize(new Dimension(200, 25));

        panel.add(new JLabel(Language.get("warehouse.name")+":"));
        panel.add(warehouseBox);
        panel.add(new JLabel(Language.get("item.item")+":"));
        panel.add(itemBox);
        panel.add(removeBtn);

        try (org.hibernate.Session session = Database.getSessionFactory().openSession()) {
            List<Warehouse> warehouses = session.createQuery("FROM Warehouse", Warehouse.class).list();
            for (Warehouse w : warehouses) warehouseBox.addItem(w);
        }

        warehouseBox.addActionListener(e -> {
            itemBox.removeAllItems();
            Warehouse selected = (Warehouse) warehouseBox.getSelectedItem();
            if (selected == null) return;

            try (org.hibernate.Session session = Database.getSessionFactory().openSession()) {
                List<Inventory> inventoryList = session.createQuery(
                                "FROM Inventory WHERE warehouse_id = :wid", Inventory.class)
                        .setParameter("wid", selected.getId())
                        .list();

                for (Inventory inv : inventoryList) {
                    Item item = session.get(Item.class, inv.getItemId());
                    if (item != null) itemBox.addItem(item);
                }
            }
        });

        removeBtn.addActionListener(e -> {
            Warehouse warehouse = (Warehouse) warehouseBox.getSelectedItem();
            Item item = (Item) itemBox.getSelectedItem();
            if (warehouse == null || item == null) return;

            Inventory.Pk pk = new Inventory.Pk(warehouse.getId(), item.getId());
            Inventory dummy = new Inventory();
            dummy.setWarehouseId(pk.warehouse_id);
            dummy.setItemId(pk.item_id);

            App.getInstance().setScreen(new ManagePanelWindow(dummy));
        });

        // Ensure first warehouse triggers item load
        if (warehouseBox.getItemCount() > 0) {
            warehouseBox.setSelectedIndex(0); // this fires the action listener and populates itemBox
        }

        return panel;
    }





    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBackground(new Color(70, 130, 180));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
