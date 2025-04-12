
package logic;

import db.Database;

import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Properties;

public class Launcher extends JFrame {

    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final DBConfig config = new DBConfig();
    private final JPanel dbFormPanel;

    String[] dbmsOptions = {"MySQL", "PostgreSQL", "SQLServer", "SQLite"};
    String[] logoPath = {"/mysql_logo.png", "/postgresql_logo.png", "/sqlserver_logo.png", "/sqlite_logo.png"};

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

        JLabel statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/logo.png"));
        Image scaledLogo = logoIcon.getImage().getScaledInstance(280, 280, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(Box.createVerticalStrut(20));
        topPanel.add(logoLabel);
        topPanel.add(Box.createVerticalStrut(10));

        JPanel buttonsPanel = new JPanel();
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
                launchApp();
            } catch (Exception ex) {
                logErrorToFile(ex);
                statusLabel.setText("Failed to launch application. Check error log.");
                Timer timer = new Timer(3000, evt -> {
                    statusLabel.setText(" ");
                    ((Timer) evt.getSource()).stop(); // Stop the timer after one run
                });
                timer.start();
            }
        });


        buttonsPanel.add(fullscreenButton);
        buttonsPanel.add(Box.createVerticalStrut(10));
        buttonsPanel.add(configButton);
        buttonsPanel.add(Box.createVerticalStrut(10));
        buttonsPanel.add(startButton);
        buttonsPanel.add(Box.createVerticalStrut(20));
        buttonsPanel.add(statusLabel);

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



        int gridX = 0, gridY = 0;
        for (int i = 0; i < dbmsOptions.length; i++) {
            String dbms = dbmsOptions[i];
            URL imageUrl = getClass().getResource(logoPath[i]);
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
        JPanel DBpanel = new JPanel();
        DBpanel.setLayout(new BoxLayout(DBpanel, BoxLayout.Y_AXIS));
        DBpanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        int imgNo = switch (dbms) {
            case "MySQL" -> 0;
            case "PostgreSQL" -> 1;
            case "SQLServer" -> 2;
            case "SQLite" -> 3;
            default -> throw new IllegalStateException("Unexpected value: " + dbms);
        };
        URL imageUrl = getClass().getResource(logoPath[imgNo]);
        ImageIcon icon = imageUrl != null ? new ImageIcon(imageUrl) : new ImageIcon();
        JButton imageButton = new JButton(new ImageIcon(icon.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH)));
        imageButton.setPreferredSize(new Dimension(100, 100));
        imageButton.setContentAreaFilled(false);
        imageButton.setBorderPainted(false);
        imageButton.setFocusPainted(false);
        imageButton.setOpaque(false);
        imageButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        DBpanel.add(imageButton);



        boolean isSQLite = dbms.equalsIgnoreCase("SQLite");

        JTextField hostField, portField, dbField, userField;
        JPasswordField passField;

        JLabel statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (isSQLite) {
            passField = null;
            userField = null;
            portField = null;
            hostField = null;
            dbField = addFormRow("Database Path:", "warehouse_db.sqlite", DBpanel);
            JButton browseButton = createStyledButton("Browse");
            browseButton.addActionListener(e -> {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Select SQLite DB File");
                if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    dbField.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            });
            DBpanel.add(Box.createVerticalStrut(10));
            DBpanel.add(browseButton);
        } else {
            hostField = addFormRow("Host:", "localhost", DBpanel);
            portField = addFormRow("Port:", getDefaultPort(dbms), DBpanel);
            dbField = addFormRow("Database Name:", "warehouse_db", DBpanel);
            userField = addFormRow("Username:", "root", DBpanel);
            passField = addPasswordRow(DBpanel);
        }

        JButton saveBtn = createStyledButton("Save Configuration");
        saveBtn.addActionListener(e -> {
            fillConfig(dbms, isSQLite, hostField, portField, dbField, userField, passField);
            saveConfigToFile();
            statusLabel.setText("Configuration saved for " + dbms);
            Timer timer = new Timer(3000, evt -> {
                statusLabel.setText(" ");
                ((Timer) evt.getSource()).stop(); // Stop the timer after one run
            });
            timer.start();
        });

        JButton launchBtn = createStyledButton("Launch Without Saving");
        launchBtn.addActionListener(e -> {
            fillConfig(dbms, isSQLite, hostField, portField, dbField, userField, passField);
            try {
                launchApp();
            } catch (Exception exception) {
                statusLabel.setText("Failed to launch application. Check error log.");
                Timer timer = new Timer(3000, evt -> {
                    statusLabel.setText(" ");
                    ((Timer) evt.getSource()).stop(); // Stop the timer after one run
                });
                timer.start();
                logErrorToFile(exception);
            }
        });

        JButton backBtn = createStyledButton("Back");
        backBtn.addActionListener(e -> cardLayout.show(cardPanel, "DBMSSelection"));

        DBpanel.add(Box.createVerticalStrut(10));
        DBpanel.add(saveBtn);
        DBpanel.add(Box.createVerticalStrut(10));
        DBpanel.add(launchBtn);
        DBpanel.add(Box.createVerticalStrut(10));
        DBpanel.add(backBtn);


        DBpanel.add(Box.createVerticalStrut(10));
        DBpanel.add(statusLabel);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.add(DBpanel);
        return wrapper;
    }



    private JTextField addFormRow(String label, String defaultValue, JPanel parent) {
        JPanel row = new JPanel(new GridBagLayout());
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


    private JPasswordField addPasswordRow(JPanel parent) {
        JPanel row = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel jLabel = new JLabel("Password:");
        jLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        jLabel.setPreferredSize(new Dimension(120, 25)); // consistent label width

        JPasswordField field = new JPasswordField("password");
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
            if (config.host != null) props.setProperty("host", config.host);
            if (config.port != null) props.setProperty("port", config.port);
            if (config.username != null) props.setProperty("username", config.username);
            if (config.password != null) props.setProperty("password", config.password);
        } else {
            props.setProperty("host", "null");
            props.setProperty("port", "null");
            props.setProperty("username", "null");
            props.setProperty("password", "null");
        }

        try (FileOutputStream out = new FileOutputStream("config.properties")) {
            props.store(out, null);
        } catch (IOException e) {
            logErrorToFile(e);

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
            logErrorToFile(e);

        }
    }

    private void fillConfig(String dbms, boolean isSQLite,
                            JTextField hostField, JTextField portField,
                            JTextField dbField, JTextField userField, JPasswordField passField) {
        config.dbms = dbms;
        config.dbName = dbField.getText().trim();

        if (isSQLite) {
            config.host = config.port = config.username = config.password = "null";
        } else {
            config.host = hostField.getText().trim();
            config.port = portField.getText().trim();
            config.username = userField.getText().trim();
            config.password = new String(passField.getPassword()).trim();
        }
    }

    private void launchApp() throws Exception {
        Database.connect(
                config.dbms, config.host, config.port,
                config.dbName, config.username, config.password
        );
        dispose();
        new Session();
        Session.getInstance().setDBMS(config.dbms);
        Session.getInstance().setFullscreen(isFullscreen);
        new App();
    }

    private void logErrorToFile(Exception e) {
        try (FileOutputStream fos = new FileOutputStream("errorlog.txt", true)) {
            StringBuilder logMessage = new StringBuilder("[" + LocalDateTime.now() + "] " +
                    e.toString() + "\n");
            for (StackTraceElement ste : e.getStackTrace()) {
                logMessage.append("\tat ").append(ste.toString()).append("\n");
            }
            logMessage.append("\n");
            fos.write(logMessage.toString().getBytes());
        } catch (IOException ioEx) {
            ioEx.printStackTrace(); // fallback to console
        }
    }

}
