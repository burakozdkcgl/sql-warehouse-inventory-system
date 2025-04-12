
package logic;

import db.Database;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.sql.SQLException;
import java.io.*;
import java.util.Properties;

public class Launcher extends JFrame {

    private CardLayout cardLayout;
    private JPanel cardPanel;
    private DBConfig config = new DBConfig();
    private JPanel dbFormPanel;

    private boolean isFullscreen = true;

    public Launcher() {
        setTitle("Warehouse Inventory System");
        getContentPane().setPreferredSize(new Dimension(400, 500));
        pack();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(createLauncherPanel(), "Launcher");
        cardPanel.add(getDBMSSelectionPanel(), "DBMSSelection");

        dbFormPanel = new JPanel(new BorderLayout());
        cardPanel.add(dbFormPanel, "DBMSForm");

        add(cardPanel);
        cardLayout.show(cardPanel, "Launcher");
        setVisible(true);
    }

    private JPanel createLauncherPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(240, 240, 240));

        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(240, 240, 240));
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/logo.png"));
        Image scaledLogo = logoIcon.getImage().getScaledInstance(280, 280, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(Box.createVerticalStrut(20));
        topPanel.add(logoLabel);
        topPanel.add(Box.createVerticalStrut(10));

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBackground(new Color(240, 240, 240));
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));

        JButton fullscreenButton = createStyledButton("Fullscreen: Enabled");
        JButton configButton = createStyledButton("Configure Database");
        JButton startButton = createStyledButton("Start Application");

        configButton.addActionListener(e -> cardLayout.show(cardPanel, "DBMSSelection"));

        fullscreenButton.addActionListener(e -> {
            if (isFullscreen) fullscreenButton.setText("Fullscreen: Disabled");
            else fullscreenButton.setText("Fullscreen: Enabled");
            isFullscreen = !isFullscreen;
        });

        startButton.addActionListener(e -> {
            loadConfigFromFile();
            try {
                Database.connect(
                        config.dbms, config.host, config.port,
                        config.dbName, config.username, config.password
                );
                dispose();
                new Session();
                Session.getInstance().setDBMS(config.dbms);
                Session.getInstance().setFullscreen(isFullscreen);
                new App();
            } catch (SQLException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this,
                        "Database connection failed:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonsPanel.add(fullscreenButton);
        buttonsPanel.add(Box.createVerticalStrut(10));
        buttonsPanel.add(configButton);
        buttonsPanel.add(Box.createVerticalStrut(10));
        buttonsPanel.add(startButton);
        buttonsPanel.add(Box.createVerticalStrut(20));

        panel.add(Box.createVerticalGlue());
        panel.add(topPanel);
        panel.add(Box.createVerticalGlue());
        panel.add(buttonsPanel);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel getDBMSSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel imagePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(30, 30, 30, 30);

        String[] dbmsOptions = {"MySQL", "PostgreSQL", "SQLServer", "SQLite"};
        String[] imageNames = {"/mysql_logo.png", "/postgresql_logo.png", "/sqlserver_logo.png", "/sqlite_logo.png"};

        int gridX = 0, gridY = 0;
        for (int i = 0; i < dbmsOptions.length; i++) {
            String dbms = dbmsOptions[i];
            URL imageUrl = getClass().getResource(imageNames[i]);
            ImageIcon icon = imageUrl != null ? new ImageIcon(imageUrl) : new ImageIcon();

            JButton imageButton = new JButton(new ImageIcon(icon.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH)));
            imageButton.setPreferredSize(new Dimension(100, 100));
            imageButton.setContentAreaFilled(false);
            imageButton.setBorderPainted(false);
            imageButton.setFocusPainted(false);
            imageButton.setOpaque(false);

            imageButton.addActionListener(e -> {
                config.dbms = dbms;
                dbFormPanel.removeAll();
                dbFormPanel.add(getDBFormPanel(dbms), BorderLayout.CENTER);
                dbFormPanel.revalidate();
                dbFormPanel.repaint();
                cardLayout.show(cardPanel, "DBMSForm");
            });

            imageButton.addMouseListener(new java.awt.event.MouseAdapter() {
                Timer enlargeTimer, shrinkTimer;
                int currentSize = 90;

                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    if (shrinkTimer != null && shrinkTimer.isRunning()) shrinkTimer.stop();
                    enlargeTimer = new Timer(20, null);
                    enlargeTimer.addActionListener(e -> {
                        if (currentSize < 100) {
                            currentSize += 2;
                            Image scaled = icon.getImage().getScaledInstance(currentSize, currentSize, Image.SCALE_SMOOTH);
                            imageButton.setIcon(new ImageIcon(scaled));
                            imageButton.setPreferredSize(new Dimension(currentSize + 10, currentSize + 10));
                            imageButton.revalidate();
                        } else {
                            enlargeTimer.stop();
                        }
                    });
                    enlargeTimer.start();
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    if (enlargeTimer != null && enlargeTimer.isRunning()) enlargeTimer.stop();
                    shrinkTimer = new Timer(20, null);
                    shrinkTimer.addActionListener(e -> {
                        if (currentSize > 90) {
                            currentSize -= 2;
                            Image scaled = icon.getImage().getScaledInstance(currentSize, currentSize, Image.SCALE_SMOOTH);
                            imageButton.setIcon(new ImageIcon(scaled));
                            imageButton.setPreferredSize(new Dimension(currentSize + 10, currentSize + 10));
                            imageButton.revalidate();
                        } else {
                            shrinkTimer.stop();
                        }
                    });
                    shrinkTimer.start();
                }
            });

            gbc.gridx = gridX;
            gbc.gridy = gridY;
            imagePanel.add(imageButton, gbc);
            if (++gridX > 1) { gridX = 0; gridY++; }
        }

        JButton backBtn = createStyledButton("Back");
        backBtn.addActionListener(e -> cardLayout.show(cardPanel, "Launcher"));

        JPanel southPanel = new JPanel();
        southPanel.add(backBtn);

        panel.add(imagePanel, BorderLayout.CENTER);
        panel.add(southPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel getDBFormPanel(String dbms) {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        formPanel.setBackground(new Color(245, 245, 245));

        JLabel title = new JLabel("Configure " + dbms + " Connection");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(title);
        formPanel.add(Box.createVerticalStrut(20));

        JTextField hostField = null, portField = null, dbField, userField = null;
        JPasswordField passField = null;

        if (!dbms.equalsIgnoreCase("SQLite")) {
            hostField = addFormRow("Host:", "localhost", formPanel);
            portField = addFormRow("Port:", getDefaultPort(dbms), formPanel);
            dbField = addFormRow("Database Name:", "warehouse_db", formPanel);
            userField = addFormRow("Username:", "root", formPanel);
            passField = addPasswordRow("Password:", "password", formPanel);
        } else {
            dbField = addFormRow("Database File Path:", "warehouse_db.sqlite", formPanel);
        }

        formPanel.add(Box.createVerticalStrut(20));

        JButton saveBtn = createStyledButton("Save Configuration");
        JTextField finalUserField = userField;
        JTextField finalPortField = portField;
        JTextField finalHostField = hostField;
        JPasswordField finalPassField = passField;
        saveBtn.addActionListener(e -> {
            config.dbName = dbField.getText().trim();
            if (!dbms.equalsIgnoreCase("SQLite")) {
                config.host = finalHostField.getText().trim();
                config.port = finalPortField.getText().trim();
                config.username = finalUserField.getText().trim();
                config.password = new String(finalPassField.getPassword()).trim();
            } else {
                config.host = config.port = config.username = config.password = "null";
            }
            saveConfigToFile();
            JOptionPane.showMessageDialog(this, "Configuration saved for " + dbms);
            cardLayout.show(cardPanel, "Launcher");
        });

        JButton backBtn = createStyledButton("Back");
        backBtn.addActionListener(e -> cardLayout.show(cardPanel, "DBMSSelection"));


        JButton launchBtn = createStyledButton("Launch without Saving Config");
        launchBtn.setMaximumSize(new Dimension(250, 40)); // Wider than the default
        launchBtn.addActionListener(e -> {
            config.dbName = dbField.getText().trim();
            if (!dbms.equalsIgnoreCase("SQLite")) {
                config.host = finalHostField.getText().trim();
                config.port = finalPortField.getText().trim();
                config.username = finalUserField.getText().trim();
                config.password = new String(finalPassField.getPassword()).trim();
            } else {
                config.host = config.port = config.username = config.password = "null";
            }

            try {
                Database.connect(
                        config.dbms, config.host, config.port,
                        config.dbName, config.username, config.password
                );
                dispose();
                new Session();
                Session.getInstance().setDBMS(config.dbms);
                Session.getInstance().setFullscreen(isFullscreen);
                new App();
            } catch (SQLException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this,
                        "Database connection failed:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        formPanel.add(saveBtn);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(launchBtn);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(backBtn);


        return formPanel;
    }


    private JTextField addFormRow(String label, String defaultValue, JPanel parent) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        jLabel.setPreferredSize(new Dimension(120, 25)); // consistent label width

        JTextField field = new JTextField(defaultValue);
        field.setPreferredSize(new Dimension(200, 30));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(1, 0, 1, 10); // tighter spacing
        row.add(jLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        row.add(field, gbc);


        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));

        parent.add(row);
        return field;
    }


    private JPasswordField addPasswordRow(String label, String defaultValue, JPanel parent) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        jLabel.setPreferredSize(new Dimension(120, 25)); // consistent label width

        JPasswordField field = new JPasswordField(defaultValue);
        field.setPreferredSize(new Dimension(200, 30));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(1, 0, 1, 10);
        row.add(jLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        row.add(field, gbc);

        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));

        parent.add(row);
        return field;
    }



    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(100, 100, 100));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setMaximumSize(new Dimension(200, 40));
        button.getModel().addChangeListener(e -> {
            ButtonModel model = button.getModel();
            if (model.isPressed()) button.setBackground(new Color(80, 80, 80));
            else if (model.isRollover()) button.setBackground(new Color(120, 120, 120));
            else button.setBackground(new Color(100, 100, 100));
        });
        return button;
    }

    private String getDefaultPort(String dbms) {
        return switch (dbms) {
            case "MySQL" -> "3306";
            case "PostgreSQL" -> "5432";
            case "SQLServer" -> "1433";
            default -> "";
        };
    }

    static class DBConfig {
        String dbms, host, port, dbName, username, password;
    }

    private void saveConfigToFile() {
        Properties props = new Properties();
        props.setProperty("dbms", config.dbms);
        props.setProperty("dbName", config.dbName);
        boolean isSQLite = "SQLite".equalsIgnoreCase(config.dbms);
        if (!isSQLite) {
            props.setProperty("host", config.host);
            props.setProperty("port", config.port);
            props.setProperty("username", config.username);
            props.setProperty("password", config.password);
        } else {
            props.setProperty("host", "null");
            props.setProperty("port", "null");
            props.setProperty("username", "null");
            props.setProperty("password", "null");
        }

        try (FileOutputStream out = new FileOutputStream("config.properties")) {
            props.store(out, null);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to save config:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadConfigFromFile() {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream("config.properties")) {
            props.load(in);
            config.dbms = props.getProperty("dbms");
            config.host = props.getProperty("host");
            config.port = props.getProperty("port");
            config.dbName = props.getProperty("dbName");
            config.username = props.getProperty("username");
            config.password = props.getProperty("password");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to load config:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
