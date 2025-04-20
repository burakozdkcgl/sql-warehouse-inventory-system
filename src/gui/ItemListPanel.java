package gui;

import db.Database;
import entity.Item;
import org.hibernate.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ItemListPanel extends JPanel {


    public ItemListPanel() {
        setLayout(new GridBagLayout());
        setOpaque(false);

        JPanel whiteBox = new JPanel(new BorderLayout(10, 10));
        whiteBox.setBackground(Color.WHITE);
        whiteBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(20, 20, 20, 20)
        ));
        whiteBox.setPreferredSize(new Dimension(950, 620));

        JLabel title = new JLabel("Item List by Category", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(40, 40, 40));
        whiteBox.add(title, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        Map<String, List<Item>> categorizedItems = fetchItemsByCategory();

        for (String category : categorizedItems.keySet()) {
            JPanel categoryPanel = new JPanel(new BorderLayout(5, 5));
            categoryPanel.setBackground(new Color(250, 250, 250));
            categoryPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220)),
                    new EmptyBorder(10, 10, 10, 10)
            ));

            JLabel categoryLabel = new JLabel(category);
            categoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            categoryLabel.setForeground(new Color(60, 60, 60));
            categoryPanel.add(categoryLabel, BorderLayout.NORTH);

            JTable table = createStyledTable();
            DefaultTableModel model = (DefaultTableModel) table.getModel();

            for (Item item : categorizedItems.get(category)) {
                model.addRow(new Object[]{
                        item.getSku(), item.getName(), item.getDescription()
                });
            }

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(850, 200));
            categoryPanel.add(scrollPane, BorderLayout.CENTER);

            contentPanel.add(categoryPanel);
            contentPanel.add(Box.createVerticalStrut(20));
        }

        JScrollPane itemScrollPane = new JScrollPane(contentPanel);
        itemScrollPane.setBorder(null);
        whiteBox.add(itemScrollPane, BorderLayout.CENTER);
        whiteBox.add(Box.createVerticalStrut(20), BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(whiteBox);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane);
    }


    private JTable createStyledTable() {
        String[] columns = {"SKU", "Name", "Description"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        JTable table = new JTable(model);
        table.setRowHeight(24);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(220, 235, 245));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, val, sel, foc, row, col);
                c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                return c;
            }
        });

        table.getColumnModel().getColumn(0).setPreferredWidth(100);  // SKU
        table.getColumnModel().getColumn(1).setPreferredWidth(150);  // Name
        table.getColumnModel().getColumn(2).setPreferredWidth(600);  // Description

        return table;
    }

    private Map<String, List<Item>> fetchItemsByCategory() {
        Map<String, List<Item>> map = new TreeMap<>();
        try (Session session = Database.getSessionFactory().openSession()) {
            List<Item> items = session.createQuery("FROM Item", Item.class).list();
            map = items.stream().collect(Collectors.groupingBy(Item::getCategory, TreeMap::new, Collectors.toList()));
        } catch (Exception e) {
            logic.ErrorLogger.log(e);
            JOptionPane.showMessageDialog(this, "Failed to load items.\nSee error log.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return map;
    }
}
