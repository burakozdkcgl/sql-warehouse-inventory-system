package gui;

import db.Database;
import entity.Inventory;
import entity.Item;
import entity.Warehouse;
import jakarta.persistence.TypedQuery;
import org.hibernate.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InventoryPanel extends JPanel {

    public InventoryPanel() {
        setLayout(new GridBagLayout());
        setOpaque(false);

        JPanel whiteBox = new JPanel(new BorderLayout(10, 10));
        whiteBox.setBackground(Color.WHITE);
        whiteBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(20, 20, 20, 20)
        ));
        whiteBox.setPreferredSize(new Dimension(950, 620));

        JLabel title = new JLabel("Inventory Overview", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(40, 40, 40));
        whiteBox.add(title, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        try (Session session = Database.getSessionFactory().openSession()) {
            List<Warehouse> warehouses = session.createQuery("FROM Warehouse", Warehouse.class).getResultList();

            for (Warehouse warehouse : warehouses) {
                JPanel warehousePanel = new JPanel(new BorderLayout(5, 5));
                warehousePanel.setBackground(new Color(250, 250, 250));
                warehousePanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 220, 220)),
                        new EmptyBorder(10, 10, 10, 10)
                ));

                JLabel warehouseLabel = new JLabel("Warehouse: " + warehouse.getName());
                warehouseLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                warehouseLabel.setForeground(new Color(60, 60, 60));
                warehousePanel.add(warehouseLabel, BorderLayout.NORTH);

                DefaultTableModel model = new DefaultTableModel();
                model.setColumnIdentifiers(new String[]{
                        "Category", "SKU", "Item Name", "Quantity", "Reorder Level"
                });

                TypedQuery<Inventory> inventoryQuery = session.createQuery(
                        "FROM Inventory WHERE warehouse_id = :wid", Inventory.class);
                inventoryQuery.setParameter("wid", warehouse.getId());

                for (Inventory inventory : inventoryQuery.getResultList()) {
                    Item item = session.get(Item.class, inventory.getItemId());
                    model.addRow(new Object[]{
                            item.getCategory(), item.getSku(), item.getName(),
                             inventory.getQuantity(), inventory.getReorderLevel()
                    });
                }

                JTable table = new JTable(model);
                table.setRowHeight(24);
                table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                table.setSelectionBackground(new Color(220, 235, 245));

                // Zebra striping
                table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable tbl, Object val, boolean sel, boolean foc, int row, int col) {
                        Component c = super.getTableCellRendererComponent(tbl, val, sel, foc, row, col);
                        c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                        return c;
                    }
                });

                JScrollPane scrollPane = new JScrollPane(table);
                scrollPane.setPreferredSize(new Dimension(850, 200));
                warehousePanel.add(scrollPane, BorderLayout.CENTER);

                contentPanel.add(warehousePanel);
                contentPanel.add(Box.createVerticalStrut(20));
            }
        } catch (Exception e) {
            e.printStackTrace();
            contentPanel.add(new JLabel("Error loading inventory data."));
        }

        JScrollPane inventoryScrollPane = new JScrollPane(contentPanel);
        inventoryScrollPane.setBorder(null);
        whiteBox.add(inventoryScrollPane, BorderLayout.CENTER);
        whiteBox.add(Box.createVerticalStrut(20), BorderLayout.SOUTH);


        JScrollPane scrollPane = new JScrollPane(whiteBox);
        scrollPane.setBorder(null); // optional: no outer border
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // smooth scroll
        add(scrollPane);
    }
}
