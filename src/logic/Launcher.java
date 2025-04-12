package logic;

import db.Database;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class Launcher extends JFrame {

    public Launcher() {
        setTitle("Warehouse Inventory System");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(7, 2, 10, 10));

        JTextField hostField = new JTextField("localhost");
        JTextField portField = new JTextField("3306");
        JTextField dbField = new JTextField("warehouse_db");
        JTextField userField = new JTextField("root");
        JPasswordField passField = new JPasswordField("password");

        JCheckBox fullscreenCheckbox = new JCheckBox("Enable fullscreen", true);
        JButton startButton = new JButton("Start");
        String[] dbmsOptions = {"MySQL", "PostgreSQL", "SQLServer"};
        JComboBox<String> dbmsSelector = new JComboBox<>(dbmsOptions);


        add(new JLabel("DB Host:")); add(hostField);
        add(new JLabel("Port:")); add(portField);
        add(new JLabel("Database Name:")); add(dbField);
        add(new JLabel("Username:")); add(userField);
        add(new JLabel("Password:")); add(passField);
        add(new JLabel("Database Type:")); add(dbmsSelector);
        add(fullscreenCheckbox); add(startButton);

        startButton.addActionListener(e -> {
            String host = hostField.getText().trim();
            String port = portField.getText().trim();
            String dbName = dbField.getText().trim();
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();

            boolean isFullscreen = fullscreenCheckbox.isSelected();
            try {
                // Attempt to get a connection
                String dbms = (String) dbmsSelector.getSelectedItem();
                Database.connect(dbms, host, port, dbName, username, password);
                System.out.println("za");
                Database.close();
                // Only dispose and start app if connection is successful
                dispose();
                new App(isFullscreen);
            } catch (SQLException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "Database connection failed:\n" + ex.getMessage().replaceAll("(.{80})", "$1\n"),
                        "", JOptionPane.ERROR_MESSAGE);

            }
        });

        setVisible(true);
    }
}
