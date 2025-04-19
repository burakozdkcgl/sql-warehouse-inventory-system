package gui;

import db.Database;
import entity.Item;
import logic.App;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ItemListPanel extends JPanel {

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Image bg = new ImageIcon(getClass().getResource("/test1.png")).getImage();
        g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
    }

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

        JLabel title = new JLabel("Item List", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(40, 40, 40));
        whiteBox.add(title, BorderLayout.NORTH);

        JTable table = new JTable();
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
        scrollPane.setPreferredSize(new Dimension(850, 450));
        whiteBox.add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        backButton.setBackground(new Color(220, 220, 220));
        backButton.setFocusPainted(false);
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> App.getInstance().setScreen(new MainPanel()));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(backButton);
        whiteBox.add(buttonPanel, BorderLayout.SOUTH);


        add(whiteBox);
        loadItemsIntoTable(table);
    }

    private void loadItemsIntoTable(JTable table) {
        String[] columns = {"ID", "Name", "SKU", "Category", "Description"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        table.setModel(model);

        try (Session session = Database.getSessionFactory().openSession()) {
            List<Item> items = session.createQuery("FROM Item", Item.class).list();
            for (Item item : items) {
                model.addRow(new Object[]{
                        item.getId(), item.getName(), item.getSku(),
                        item.getCategory(), item.getDescription()
                });
            }
        } catch (Exception e) {
            logic.ErrorLogger.log(e);
            JOptionPane.showMessageDialog(this, "Failed to load items.\nSee error log.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
