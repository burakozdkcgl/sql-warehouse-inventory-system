package gui;

import db.Database;
import entity.User;
import logic.Session;
import org.hibernate.query.Query;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainPanel extends JPanel {

    public MainPanel() {
        setLayout(new GridBagLayout());
        setOpaque(false);

        User user = Session.getInstance().getCurrentUser();
        if (user == null) return;

        JPanel whiteBox = new JPanel(new BorderLayout(10, 10));
        whiteBox.setBackground(Color.WHITE);
        whiteBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(20, 20, 20, 20)
        ));
        whiteBox.setPreferredSize(new Dimension(950, 620));

        // Header
        JLabel welcomeLabel = new JLabel("Welcome, " + user.getName(), SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        whiteBox.add(welcomeLabel, BorderLayout.NORTH);

        // Top Stats
        JPanel statsRow = new JPanel(new GridLayout(1, 5, 15, 10));
        statsRow.add(createStatCard("Item Types", getCount("Item")));
        statsRow.add(createStatCard("Below Reorder", getItemsBelowReorderCount()));
        statsRow.add(createStatCard("Warehouses", getCount("Warehouse")));
        statsRow.add(createStatCard("Users", getCount("User")));
        statsRow.add(createStatCard("Pending Orders", getPendingOrderCount()));



        JPanel statsWrapper = new JPanel(new BorderLayout());
        statsWrapper.setPreferredSize(new Dimension(920, 50)); // adjust height as needed
        statsWrapper.add(statsRow, BorderLayout.CENTER);
        whiteBox.add(statsWrapper, BorderLayout.CENTER);


        // Middle Content
        JPanel middle = new JPanel(new GridLayout(1, 2, 15, 10));
        middle.setPreferredSize(new Dimension(920, 250));

        middle.add(createItemWarehousePieChart());
        middle.add(createTitledScrollTable("Items Below Reorder Level", getLowStockModel()));

        // Bottom Content
        JPanel bottom = new JPanel(new GridLayout(1, 2, 15, 10));
        bottom.setPreferredSize(new Dimension(920, 200));

        bottom.add(createTitledScrollTable("Recently Created Orders", getRecentOrdersModel()));
        bottom.add(createTopItemsBarChart());


        // Combine middle + bottom
        JPanel bottomCombined = new JPanel();
        bottomCombined.setLayout(new BoxLayout(bottomCombined, BoxLayout.Y_AXIS));
        bottomCombined.add(middle);
        bottomCombined.add(Box.createVerticalStrut(10));
        bottomCombined.add(bottom);

        whiteBox.add(bottomCombined, BorderLayout.SOUTH);

        add(whiteBox);
    }

    private JPanel createStatCard(String title, int value) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setBorder(new EmptyBorder(5, 0, 0, 0));

        JLabel valueLabel = new JLabel(String.valueOf(value), SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(new Color(50, 120, 180));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);

        return panel;
    }

    private int getCount(String entityName) {
        try (org.hibernate.Session session = Database.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(e) FROM " + entityName + " e", Long.class);
            return query.uniqueResult().intValue();
        }
    }

    private int getPendingOrderCount() {
        try (org.hibernate.Session session = Database.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(o) FROM Order o WHERE o.status = 'Pending'", Long.class);
            return query.uniqueResult().intValue();
        }
    }

    private int getItemsBelowReorderCount() {
        try (org.hibernate.Session session = Database.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(inv) FROM Inventory inv WHERE inv.quantity < inv.reorder_level", Long.class);
            return query.uniqueResult().intValue();
        }
    }


    private ChartPanel createItemWarehousePieChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        try (org.hibernate.Session session = Database.getSessionFactory().openSession()) {
            List<Object[]> results = session.createQuery(
                            "SELECT w.name, SUM(inv.quantity) " +
                                    "FROM Inventory inv JOIN Warehouse w ON inv.warehouse_id = w.id " +
                                    "GROUP BY w.name", Object[].class)
                    .list();

            results.sort((a, b) -> Long.compare((Long) b[1], (Long) a[1]));

            int limit = 5;
            long otherSum = 0;
            for (int i = 0; i < results.size(); i++) {
                if (i < limit) {
                    dataset.setValue((String) results.get(i)[0], (Long) results.get(i)[1]);
                } else {
                    otherSum += (Long) results.get(i)[1];
                }
            }
            if (otherSum > 0) {
                dataset.setValue("Other", otherSum);
            }

        }

        JFreeChart chart = ChartFactory.createPieChart("Item Distribution Across Warehouses", dataset, true, true, false);
        ((org.jfree.chart.plot.PiePlot) chart.getPlot()).setLabelFont(new Font("SansSerif", Font.PLAIN, 10));
        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 12));

        chart.setBackgroundPaint(Color.WHITE);
        chart.getPlot().setBackgroundPaint(Color.WHITE);

        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new Dimension(440, 250));
        panel.setOpaque(false);
        return panel;
    }


    private ChartPanel createTopItemsBarChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try (org.hibernate.Session session = Database.getSessionFactory().openSession()) {
            List<Object[]> results = session.createQuery(
                            "SELECT i.name, SUM(inv.quantity) " +
                                    "FROM Inventory inv JOIN Item i ON inv.item_id = i.id " +
                                    "GROUP BY i.name ORDER BY SUM(inv.quantity) DESC", Object[].class)
                    .setMaxResults(5)
                    .list();

            for (Object[] row : results) {
                String itemName = (String) row[0];
                if (itemName.length() > 15) itemName = itemName.substring(0, 15) + "...";
                Long quantity = (Long) row[1];
                dataset.addValue(quantity, "Quantity", itemName);
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Top 5 Stocked Items", "", "", dataset,
                org.jfree.chart.plot.PlotOrientation.VERTICAL, false, true, false);

        CategoryPlot plot = chart.getCategoryPlot();

        plot.getDomainAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 10));
        plot.getRangeAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 10));
        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 12));

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setMaximumBarWidth(0.1);

        plot.getDomainAxis().setCategoryLabelPositions(
                org.jfree.chart.axis.CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));

        chart.setBackgroundPaint(Color.WHITE);
        plot.setBackgroundPaint(Color.WHITE);

        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new Dimension(440, 250));
        panel.setOpaque(false);
        return panel;
    }





    private TableModel getLowStockModel() {
        String[] cols = {"Warehouse", "Item", "Quantity", "Reorder Level"};
        Object[][] data;
        try (org.hibernate.Session session = Database.getSessionFactory().openSession()) {
            Query<Object[]> query = session.createQuery(
                    "SELECT w.name, i, inv.quantity, inv.reorder_level " +
                            "FROM Inventory inv " +
                            "JOIN Item i ON inv.item_id = i.id " +
                            "JOIN Warehouse w ON inv.warehouse_id = w.id " +
                            "WHERE inv.quantity < inv.reorder_level", Object[].class);
            List<Object[]> resultList = query.list();
            data = new Object[resultList.size()][cols.length];
            for (int j = 0; j < resultList.size(); j++) {
                Object[] row = resultList.get(j);
                data[j][0] = row[0]; // warehouse name
                data[j][1] = row[1].toString(); // item.toString()
                data[j][2] = row[2]; // quantity
                data[j][3] = row[3]; // reorder level
            }
        }
        return new DefaultTableModel(data, cols) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
    }



    private TableModel getRecentOrdersModel() {
        String[] cols = {"Order ID", "Status", "Created At"};
        Object[][] data;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try (org.hibernate.Session session = Database.getSessionFactory().openSession()) {
            Query<Object[]> query = session.createQuery(
                    "SELECT o.id, o.status, o.createdAt FROM Order o ORDER BY o.createdAt DESC", Object[].class);
            List<Object[]> resultList = query.setMaxResults(5).list();
            data = new Object[resultList.size()][3];
            for (int i = 0; i < resultList.size(); i++) {
                Object[] row = resultList.get(i);
                data[i][0] = row[0]; // id
                data[i][1] = row[1]; // status
                data[i][2] = ((LocalDateTime) row[2]).format(formatter); // formatted createdAt
            }
        }

        return new DefaultTableModel(data, cols) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
    }


    private JPanel createTitledScrollTable(String title, TableModel model) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));

        JTable table = new JTable(model);
        table.setRowHeight(22);
        table.getTableHeader().setReorderingAllowed(false);
        table.setFillsViewportHeight(false); // Don't stretch it

        if ("Reorder Level".equals(model.getColumnName(3))) {
            table.getColumnModel().getColumn(0).setPreferredWidth(100); // Quantity
            table.getColumnModel().getColumn(1).setPreferredWidth(150); // Quantity
            table.getColumnModel().getColumn(2).setPreferredWidth(60); // Quantity
            table.getColumnModel().getColumn(3).setPreferredWidth(80); // Reorder Level
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(400, table.getRowCount() * 22 + 45)); // content height only

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

}
