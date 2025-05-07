package gui;

import db.Database;
import entity.Inventory;
import entity.Item;
import entity.Warehouse;
import jakarta.persistence.TypedQuery;
import org.hibernate.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InventoryPanel extends JPanel {

    private int currentIndex = 0;
    private final List<Warehouse> warehouses;
    private JTextField searchField;
    private JScrollPane tableScroll;
    private final JPanel warehouseDisplayPanel;

    public InventoryPanel() {
        setLayout(new GridBagLayout());
        setOpaque(false);

        JPanel whiteBox = new JPanel(new BorderLayout(15, 15));
        whiteBox.setBackground(Color.WHITE);
        whiteBox.setPreferredSize(new Dimension(950, 620));
        whiteBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel title = new JLabel("Inventory Overview", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(30, 30, 30));
        whiteBox.add(title, BorderLayout.NORTH);

        // Load warehouse data
        try (Session session = Database.getSessionFactory().openSession()) {
            warehouses = session.createQuery("FROM Warehouse", Warehouse.class).getResultList();
        }

        // Center panel with dynamic content
        warehouseDisplayPanel = new JPanel(new BorderLayout());
        warehouseDisplayPanel.setOpaque(false);
        whiteBox.add(warehouseDisplayPanel, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(whiteBox);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane);

        buildSearchBar();
        updateWarehouseDisplay();
    }

    private void buildSearchBar() {
        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setToolTipText("Search item name or SKU...");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refreshTableOnly(); }
            public void removeUpdate(DocumentEvent e) { refreshTableOnly(); }
            public void changedUpdate(DocumentEvent e) { refreshTableOnly(); }
        });
    }

    private void updateWarehouseDisplay() {
        warehouseDisplayPanel.removeAll();

        if (warehouses == null || warehouses.isEmpty()) {
            JLabel noWarehouseLabel = new JLabel("No warehouses found.", SwingConstants.CENTER);
            noWarehouseLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            JPanel centerPanel = new JPanel(new GridBagLayout());
            centerPanel.setOpaque(false);
            centerPanel.add(noWarehouseLabel);
            warehouseDisplayPanel.add(centerPanel, BorderLayout.CENTER);
            warehouseDisplayPanel.revalidate();
            warehouseDisplayPanel.repaint();
            return;
        }


        Warehouse warehouse = warehouses.get(currentIndex);

        // --- Warehouse Info ---
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(5, 10, 10, 10));


        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);


        JLabel name = new JLabel("Warehouse: " + warehouse.getName());
        name.setFont(labelFont);
        infoPanel.add(name);
        infoPanel.add(Box.createVerticalStrut(5));

        JLabel location = new JLabel();
        location.setFont(labelFont);
        location.setAlignmentX(Component.CENTER_ALIGNMENT);
        if (warehouse.getLocation() != null && !warehouse.getLocation().trim().isEmpty()) {
            location.setText("Location: " + warehouse.getLocation());
            location.setVisible(true);
        } else {
            location.setText(" ");
            location.setVisible(true);
        }
        infoPanel.add(location);
        infoPanel.add(Box.createVerticalStrut(5));

        JLabel description = new JLabel();
        description.setFont(labelFont);
        description.setAlignmentX(Component.CENTER_ALIGNMENT);
        if (warehouse.getDescription() != null && !warehouse.getDescription().trim().isEmpty()) {
            description.setText("Description: " + warehouse.getDescription());
            description.setVisible(true);
        } else {
            description.setText(" ");
            description.setVisible(true);
        }
        infoPanel.add(description);
        infoPanel.add(Box.createVerticalStrut(15));


        // --- Controls Panel ---
        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));
        controlsPanel.setOpaque(false);

        // Navigation buttons
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 5));
        navPanel.setOpaque(false);
        JButton leftBtn = new JButton("← Previous");
        JButton rightBtn = new JButton("Next →");
        styleNavButton(leftBtn);
        styleNavButton(rightBtn);
        if (warehouses.size() > 1) {
            navPanel.add(leftBtn);
            navPanel.add(rightBtn);
        }

        leftBtn.addActionListener(e -> {
            currentIndex = (currentIndex - 1 + warehouses.size()) % warehouses.size();
            updateWarehouseDisplay();
        });
        rightBtn.addActionListener(e -> {
            currentIndex = (currentIndex + 1) % warehouses.size();
            updateWarehouseDisplay();
        });

        // Search field
        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Search: "), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        controlsPanel.add(navPanel);
        controlsPanel.add(Box.createVerticalStrut(10));
        controlsPanel.add(searchPanel);
        controlsPanel.add(Box.createVerticalStrut(15));

        // Table
        JTable table = createInventoryTable(warehouse);
        tableScroll = new JScrollPane(table);
        tableScroll.setPreferredSize(new Dimension(880, 360));
        tableScroll.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        // Layout composition
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        for (Component comp : infoPanel.getComponents()) {
            if (comp instanceof JLabel) {
                ((JLabel) comp).setAlignmentX(Component.CENTER_ALIGNMENT);
            }
        }
        content.add(infoPanel);

        content.add(controlsPanel);
        content.add(tableScroll);

        warehouseDisplayPanel.add(content, BorderLayout.CENTER);
        warehouseDisplayPanel.revalidate();
        warehouseDisplayPanel.repaint();
    }

    private void refreshTableOnly() {
        if (warehouses == null || warehouses.isEmpty()) return;
        JTable newTable = createInventoryTable(warehouses.get(currentIndex));
        tableScroll.setViewportView(newTable);
    }

    private JTable createInventoryTable(Warehouse warehouse) {
        DefaultTableModel model = new DefaultTableModel(new Object[] {
                "Category", "SKU", "Item Name", "Quantity", "Reorder Level"
        }, 0);

        String filter = searchField.getText().trim().toLowerCase();

        try (Session session = Database.getSessionFactory().openSession()) {
            TypedQuery<Inventory> inventoryQuery = session.createQuery(
                    "FROM Inventory WHERE warehouse_id = :wid", Inventory.class);
            inventoryQuery.setParameter("wid", warehouse.getId());

            for (Inventory inv : inventoryQuery.getResultList()) {
                Item item = session.get(Item.class, inv.getItemId());
                if (!filter.isEmpty() &&
                        !(item.getName().toLowerCase().contains(filter) ||
                                item.getSku().toLowerCase().contains(filter))) {
                    continue;
                }
                model.addRow(new Object[] {
                        item.getCategory(), item.getSku(), item.getName(),
                        inv.getQuantity(), inv.getReorderLevel()
                });
            }
        }

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setSelectionBackground(new Color(200, 220, 240));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, val, sel, foc, row, col);
                int qty = (int) tbl.getValueAt(row, 3);
                int reorder = (int) tbl.getValueAt(row, 4);
                c.setBackground(qty < reorder ? new Color(255, 230, 230)
                        : row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                return c;
            }
        });

        return table;
    }

    private void styleNavButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(new Color(230, 230, 230));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 36));
    }
}
