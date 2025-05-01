package gui;

import db.Database;
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

    private final JPanel orderListPanel = new JPanel();
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

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("View Orders", getOrderListTab());
        tabs.addTab("Create Order", getCreateOrderTab());

        whiteBox.add(tabs, BorderLayout.CENTER);
        add(whiteBox);
    }

    private JScrollPane getOrderListTab() {
        orderListPanel.setLayout(new BoxLayout(orderListPanel, BoxLayout.Y_AXIS));
        orderListPanel.setOpaque(false);
        refreshOrders();
        return new JScrollPane(orderListPanel);
    }

    private void refreshOrders() {
        orderListPanel.removeAll();
        try (org.hibernate.Session session = Database.getSessionFactory().openSession()) {
            List<Order> orders = session.createQuery("FROM Order", Order.class).list();
            for (Order order : orders) {
                JPanel card = new JPanel();
                card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.GRAY),
                        new EmptyBorder(10, 10, 10, 10)
                ));
                card.setBackground(new Color(245, 245, 245));

                JLabel info = new JLabel("Order #" + order.getId() + " | Status: " + order.getStatus() +
                        " | Created by User ID: " + order.getCreatedBy());
                info.setFont(new Font("Segoe UI", Font.BOLD, 14));
                card.add(info);

                if (order.getDescription() != null) {
                    card.add(new JLabel("Description: " + order.getDescription()));
                }

                for (OrderLine line : order.getOrderLines()) {
                    String text = "- " + line.getQuantity() + " x " + line.getItem().getName();
                    if (line.getFromWarehouse() != null)
                        text += " from " + line.getFromWarehouse().getName();
                    if (line.getToWarehouse() != null)
                        text += " to " + line.getToWarehouse().getName();
                    card.add(new JLabel(text));
                }

                orderListPanel.add(card);
                orderListPanel.add(Box.createVerticalStrut(10));
            }
        }
    }

    private JScrollPane getCreateOrderTab() {
        createOrderPanel.setLayout(new BoxLayout(createOrderPanel, BoxLayout.Y_AXIS));
        createOrderPanel.setOpaque(false);

        JTextField descriptionField = new JTextField();
        descriptionField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        createOrderPanel.add(labeled("Description", descriptionField));

        JPanel orderLinesBox = new JPanel();
        orderLinesBox.setLayout(new BoxLayout(orderLinesBox, BoxLayout.Y_AXIS));
        orderLinesBox.setOpaque(false);
        createOrderPanel.add(orderLinesBox);

        JButton addItemBtn = new JButton("Add Item");
        JButton removeItemBtn = new JButton("Remove Item");
        JButton transferItemBtn = new JButton("Transfer Item");

        addItemBtn.addActionListener(e -> {
            OrderLinePanel panel = new OrderLinePanel(OrderType.ADD);
            orderLinePanels.addElement(panel);
            orderLinesBox.add(panel);
            createOrderPanel.revalidate();
        });

        removeItemBtn.addActionListener(e -> {
            OrderLinePanel panel = new OrderLinePanel(OrderType.REMOVE);
            orderLinePanels.addElement(panel);
            orderLinesBox.add(panel);
            createOrderPanel.revalidate();
        });

        transferItemBtn.addActionListener(e -> {
            OrderLinePanel panel = new OrderLinePanel(OrderType.TRANSFER);
            orderLinePanels.addElement(panel);
            orderLinesBox.add(panel);
            createOrderPanel.revalidate();
        });

        JPanel buttonsRow = new JPanel();
        buttonsRow.add(addItemBtn);
        buttonsRow.add(removeItemBtn);
        buttonsRow.add(transferItemBtn);
        createOrderPanel.add(buttonsRow);


        JButton submit = new JButton("Submit Order");
        submit.addActionListener(e -> {
            try (org.hibernate.Session session = Database.getSessionFactory().openSession()) {
                session.beginTransaction();

                Order order = new Order();
                order.setCreatedAt(LocalDateTime.now());
                order.setCreatedBy(Session.getInstance().getCurrentUser().getId());
                order.setDescription(descriptionField.getText());
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
                NotificationPanel.show(App.getInstance().getLayeredPane(), "Failed to create order! Check error log.", 3000, "red");
                App.getInstance().setScreen(new OrderPanel());
            }
        });

        createOrderPanel.add(Box.createVerticalStrut(20));
        createOrderPanel.add(submit);

        return new JScrollPane(createOrderPanel);
    }

    private JPanel labeled(String text, JComponent comp) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setOpaque(false);
        JLabel label = new JLabel(text);
        label.setPreferredSize(new Dimension(100, 25));
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
            setPreferredSize(new Dimension(880, 40));
            setMaximumSize(new Dimension(880, 40)); // adjust based on parent layout

            JPanel fieldsPanel = new JPanel(new GridLayout(1, 4, 10, 0));

            try (org.hibernate.Session session = Database.getSessionFactory().openSession()) {
                List<Item> items = session.createQuery("FROM Item", Item.class).list();
                List<Warehouse> warehouses = session.createQuery("FROM Warehouse", Warehouse.class).list();

                itemBox = new JComboBox<>(items.toArray(new Item[0]));

                fromBox = new JComboBox<>();
                toBox = new JComboBox<>();
                fromBox.addItem(null);
                toBox.addItem(null);
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
            JButton removeBtn = new JButton("✕");
            removeBtn.setPreferredSize(new Dimension(45, 30));
            removeBtn.setFocusPainted(false);
            removeBtn.addActionListener(e -> {
                Container parent = this.getParent();
                ((DefaultListModel<?>) orderLinePanels).removeElement(this);
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
