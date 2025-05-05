package gui;

import db.Database;
import db.OrderService;
import entity.*;
import logic.App;
import logic.NotificationPanel;
import logic.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderPanel extends JPanel {

    private final JPanel createOrderPanel = new JPanel();
    private final DefaultListModel<OrderLinePanel> orderLinePanels = new DefaultListModel<>();

    public OrderPanel() {
        setLayout(new GridBagLayout());
        setOpaque(false);

        JPanel whiteBox = new JPanel(new BorderLayout(10, 10));
        whiteBox.setBackground(Color.WHITE);
        whiteBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(20, 20, 20, 20)
        ));
        whiteBox.setPreferredSize(new Dimension(950, 620));

        JLabel title = new JLabel("Orders", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(40, 40, 40));
        whiteBox.add(title, BorderLayout.NORTH);


        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));



        tabs.addTab("Pending Orders", getOrderListTab("Pending"));
        tabs.addTab("Create Order", getCreateOrderTab());
        tabs.addTab("Inactive Orders", getOrderListTab("Inactive"));

        whiteBox.add(tabs, BorderLayout.CENTER);
        whiteBox.add(Box.createVerticalStrut(20), BorderLayout.SOUTH);
        add(whiteBox);

    }

    private JScrollPane getOrderListTab(String filterType) {
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        refreshOrders(filterType, listPanel);

        // Wrap listPanel in a container that allows horizontal stretching but prevents vertical fill
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.add(listPanel, BorderLayout.NORTH); // This prevents vertical stretching

        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // smooth scrolling
        return scrollPane;
    }



    private void refreshOrders(String filterType, JPanel panel) {
        panel.removeAll();
        panel.add(Box.createVerticalStrut(10));
        try (org.hibernate.Session session = Database.getSessionFactory().openSession()) {
            String hql;
            if ("Pending".equals(filterType)) {
                hql = "FROM Order WHERE status = 'Pending' ORDER BY id DESC";
            } else {
                hql = "FROM Order WHERE status = 'Canceled' OR status = 'Approved' ORDER BY id DESC";
            }

            List<Order> orders = session.createQuery(hql, Order.class).list();
            for (Order order : orders) {
                JPanel card = new JPanel();
                card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                        new EmptyBorder(12, 16, 12, 16)
                ));
                card.setBackground(new Color(245, 245, 245));

                JPanel info = new JPanel();
                info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS)); // Line-by-line

                JLabel id = new JLabel("Order #" + order.getId());
                id.setFont(new Font("Segoe UI", Font.BOLD, 14));


                info.add(id);

                if (!"Pending".equals(filterType)) {
                    info.add(createStatusLabel(order.getStatus()));
                }

                info.add(new JLabel("Created at: " + order.getCreatedAt()));

                Integer creatorId = order.getCreatedBy();
                String createdByName = "Removed User";
                if (creatorId != null) {
                    User creator = session.get(User.class, creatorId);
                    if (creator != null) {
                        createdByName = creator.getName();
                    }
                }
                info.add(new JLabel("Created by: " + createdByName));

                if (order.getDescription() != null) info.add(new JLabel("Description: " + order.getDescription()));

                for (OrderLine line : order.getOrderLines()) {
                    String text = "- " + line.getQuantity() + " x " + line.getItem().getName();
                    if (line.getFromWarehouse() != null)
                        text += " from " + line.getFromWarehouse().getName();
                    if (line.getToWarehouse() != null)
                        text += " to " + line.getToWarehouse().getName();
                    info.add(new JLabel(text));
                }


                JPanel header = new JPanel(new BorderLayout());
                header.setOpaque(false);
                header.add(info, BorderLayout.CENTER);


                if ("Pending".equals(filterType)) {
                    JButton approveBtn = new JButton("Approve Order");
                    JButton cancelBtn = new JButton("Cancel Order");
                    approveBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    approveBtn.setBackground(new Color(70, 130, 180));
                    approveBtn.setForeground(Color.WHITE);
                    approveBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                    cancelBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    cancelBtn.setBackground(new Color(70, 130, 180));
                    cancelBtn.setForeground(Color.WHITE);
                    cancelBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                    approveBtn.addActionListener(e -> {
                        OrderService.updateOrderStatus(order, "Approved");
                        App.getInstance().setScreen(new OrderPanel());
                    });
                    cancelBtn.addActionListener(e -> {
                        OrderService.updateOrderStatus(order, "Canceled");
                        App.getInstance().setScreen(new OrderPanel());
                    });

                    JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
                    buttonWrapper.setOpaque(false);
                    buttonWrapper.add(approveBtn);
                    buttonWrapper.add(cancelBtn);

                    header.add(buttonWrapper, BorderLayout.EAST);

                    card.add(header);
                } else {
                    header.add(Box.createHorizontalStrut(1), BorderLayout.EAST);
                }

                card.add(header);

                card.setMaximumSize(new Dimension(850, Integer.MAX_VALUE));
                panel.add(card);
                panel.add(Box.createVerticalStrut(10));

                panel.add(Box.createVerticalStrut(10));
            }
            panel.add(Box.createVerticalGlue());
        }
    }

    private JLabel createStatusLabel(String status) {
        String color = switch (status) {
            case "Approved" -> "green";
            case "Canceled" -> "red";
            default -> "black";
        };
        return new JLabel(String.format("<html>Status: <span style='color:%s;'>%s</span></html>", color, status));
    }



    private JScrollPane getCreateOrderTab() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(30, 50, 30, 50));
        contentPanel.setOpaque(false);

        // Description Field
        JTextField descriptionField = new JTextField();
        descriptionField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(labeled(descriptionField));
        contentPanel.add(Box.createVerticalStrut(20));

        // Order lines box
        JPanel orderLinesBox = new JPanel();
        orderLinesBox.setLayout(new BoxLayout(orderLinesBox, BoxLayout.Y_AXIS));
        orderLinesBox.setOpaque(false);
        contentPanel.add(orderLinesBox);

        // Add/Remove/Transfer buttons
        JPanel buttonsRow = getStyledButtonsRow(orderLinesBox);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(buttonsRow);
        contentPanel.add(Box.createVerticalStrut(20));



        contentPanel.add(Box.createVerticalStrut(20));

        createOrderPanel.removeAll();
        createOrderPanel.setLayout(new BorderLayout());
        createOrderPanel.add(contentPanel, BorderLayout.NORTH);

        return new JScrollPane(createOrderPanel);
    }


    private JPanel getStyledButtonsRow(JPanel orderLinesBox) {
        JButton addItemBtn = new JButton("Add Item");
        JButton removeItemBtn = new JButton("Remove Item");
        JButton transferItemBtn = new JButton("Transfer Item");
        JButton submitBtn = new JButton("Submit Order");

        for (JButton btn : new JButton[]{addItemBtn, removeItemBtn, transferItemBtn, submitBtn}) {
            btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btn.setBackground(new Color(70, 130, 180));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        addItemBtn.addActionListener(e -> {
            OrderLinePanel panel = new OrderLinePanel(OrderType.ADD);
            orderLinePanels.addElement(panel);
            orderLinesBox.add(panel);
            orderLinesBox.revalidate();
            orderLinesBox.repaint();
        });

        removeItemBtn.addActionListener(e -> {
            OrderLinePanel panel = new OrderLinePanel(OrderType.REMOVE);
            orderLinePanels.addElement(panel);
            orderLinesBox.add(panel);
            orderLinesBox.revalidate();
            orderLinesBox.repaint();
        });

        transferItemBtn.addActionListener(e -> {
            OrderLinePanel panel = new OrderLinePanel(OrderType.TRANSFER);
            orderLinePanels.addElement(panel);
            orderLinesBox.add(panel);
            orderLinesBox.revalidate();
            orderLinesBox.repaint();
        });

        submitBtn.addActionListener(e -> {
            try (org.hibernate.Session session = Database.getSessionFactory().openSession()) {
                session.beginTransaction();

                Order order = new Order();
                order.setCreatedAt(LocalDateTime.now());
                order.setCreatedBy(Session.getInstance().getCurrentUser().getId());
                order.setDescription(((JTextField) ((JPanel) ((JPanel) orderLinesBox.getParent()).getComponent(1)).getComponent(1)).getText());
                order.setStatus("Pending");

                List<OrderLine> lines = new ArrayList<>();
                for (int i = 0; i < orderLinePanels.size(); i++) {
                    OrderLinePanel olp = orderLinePanels.get(i);
                    OrderLine line = new OrderLine();
                    line.setOrder(order);
                    line.setItem((Item) olp.itemBox.getSelectedItem());
                    line.setQuantity((Integer) olp.quantityBox.getValue());
                    line.setFromWarehouse((Warehouse) olp.fromBox.getSelectedItem());
                    line.setToWarehouse((Warehouse) olp.toBox.getSelectedItem());
                    lines.add(line);
                }

                order.setOrderLines(lines);
                session.persist(order);
                session.getTransaction().commit();

                NotificationPanel.show(App.getInstance().getLayeredPane(), "Order created!", 3000, "green");
                App.getInstance().setScreen(new OrderPanel());
            } catch (Exception ex) {
                NotificationPanel.show(App.getInstance().getLayeredPane(), "Failed to create order!", 3000, "red");
                App.getInstance().setScreen(new OrderPanel());
            }
        });

        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));
        row.add(addItemBtn);
        row.add(removeItemBtn);
        row.add(transferItemBtn);
        row.add(submitBtn);

        return row;
    }


    private JPanel labeled(JComponent comp) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setOpaque(false);
        JLabel label = new JLabel("Order description: (optional)");
        label.setPreferredSize(new Dimension(200, 25));
        panel.add(label, BorderLayout.WEST);
        panel.add(comp, BorderLayout.CENTER);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return panel;
    }

    private class OrderLinePanel extends JPanel {
        JComboBox<Item> itemBox;
        JComboBox<Warehouse> fromBox;
        JComboBox<Warehouse> toBox;
        JSpinner quantityBox;

        public OrderLinePanel(OrderType type) {
            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(0, 40));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

            JPanel fieldsPanel = new JPanel(new GridLayout(1, 4, 10, 0));

            try (org.hibernate.Session session = Database.getSessionFactory().openSession()) {
                List<Item> items = session.createQuery("FROM Item", Item.class).list();
                List<Warehouse> warehouses = session.createQuery("FROM Warehouse", Warehouse.class).list();

                itemBox = new JComboBox<>(items.toArray(new Item[0]));

                fromBox = new JComboBox<>();
                toBox = new JComboBox<>();
                if (type == OrderType.ADD) {
                    fromBox.addItem(null);
                } else if (type == OrderType.REMOVE) {
                    toBox.addItem(null);
                }
                for (Warehouse w : warehouses) {
                    fromBox.addItem(w);
                    toBox.addItem(w);
                }
            }

            quantityBox = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));

            fieldsPanel.add(itemBox);
            if (type == OrderType.TRANSFER) {
                fieldsPanel.add(fromBox);
                fieldsPanel.add(toBox);
            } else if (type == OrderType.ADD) {
                fieldsPanel.add(new JLabel()); // placeholder
                fieldsPanel.add(toBox);
            } else if (type == OrderType.REMOVE) {
                fieldsPanel.add(fromBox);
                fieldsPanel.add(new JLabel()); // placeholder
            }
            fieldsPanel.add(quantityBox);

            // Create remove button
            JButton removeBtn = new JButton("X");
            removeBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            removeBtn.setBackground(new Color(70, 130, 180));
            removeBtn.setForeground(Color.WHITE);
            removeBtn.setFocusPainted(false);
            removeBtn.setPreferredSize(new Dimension(45, 30));
            removeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            removeBtn.setFocusPainted(false);
            removeBtn.addActionListener(e -> {
                Container parent = this.getParent();
                orderLinePanels.removeElement(this);
                parent.remove(this);
                parent.revalidate();
                parent.repaint();
            });

            add(fieldsPanel, BorderLayout.CENTER);
            add(removeBtn, BorderLayout.EAST);
        }

    }


    public enum OrderType {
        ADD, REMOVE, TRANSFER
    }


}
