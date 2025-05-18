package logic;

import db.Database;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.Objects;

public class Launcher extends JFrame {

    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final JPanel dbFormPanel;

    private boolean isFullscreen = true;

    private JButton langBtn, fullscreenBtn, configBtn, startBtn, backBtn;


    private static final String[] DBMS_OPTIONS = {"MySQL", "PostgreSQL", "SQLServer", "SQLite"};
    private static final String[] LOGO_PATHS = {
            "/mysql_logo.png", "/postgresql_logo.png", "/sqlserver_logo.png", "/sqlite_logo.png"
    };

    public Launcher() {
        new Session();
        Session.getInstance().setLanguage("en");
        Language.addListener(this::reloadTexts); // hook to language change


        setTitle("Warehouse Inventory System");
        setSize(400, 540); // <-- enforce size
        setLocationRelativeTo(null); // <-- center it here
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(getLauncherPanel(), "Launcher");
        cardPanel.add(getDBMSSelectionPanel(), "DBMSSelection");

        dbFormPanel = new JPanel(new BorderLayout());
        cardPanel.add(dbFormPanel, "DBMSForm");

        reloadTexts(); // <-- initialize labels on startup
        add(cardPanel);
        setVisible(true);
    }

    private JPanel getLauncherPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/logo.png"));
        Image scaledLogo = logoIcon.getImage().getScaledInstance(280, 280, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(Box.createVerticalStrut(20));
        topPanel.add(logoLabel);
        topPanel.add(Box.createVerticalStrut(10));

        langBtn = createStyledButton("");
        langBtn.addActionListener(e -> toggleLanguage());

        fullscreenBtn = createStyledButton("");
        fullscreenBtn.addActionListener(e -> toggleFullscreen(fullscreenBtn));

        configBtn = createStyledButton("");
        configBtn.addActionListener(e -> cardLayout.show(cardPanel, "DBMSSelection"));

        startBtn = createStyledButton("");
        startBtn.addActionListener(e -> {
            ConfigManager.loadConfig();
            if("SQLite".equalsIgnoreCase(ConfigManager.getDBMS())){
                String dbPath = ConfigManager.getConfig().dbName;
                File sqliteFile = new File(dbPath);
                if (!sqliteFile.exists()) {
                    showError(statusLabel, "SQLite file does not exist: " + dbPath);
                    return;
                }
            }
            try {
                launchApp();
            } catch (Exception ex) {
                showError(statusLabel, "Failed to launch application. Check connection.");
            }
        });

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.add(langBtn);
        buttonsPanel.add(Box.createVerticalStrut(10));
        buttonsPanel.add(fullscreenBtn);
        buttonsPanel.add(Box.createVerticalStrut(10));
        buttonsPanel.add(configBtn);
        buttonsPanel.add(Box.createVerticalStrut(10));
        buttonsPanel.add(startBtn);
        buttonsPanel.add(Box.createVerticalStrut(10));
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
        for (int i = 0; i < DBMS_OPTIONS.length; i++) {
            String dbms = DBMS_OPTIONS[i];
            URL imageUrl = getClass().getResource(LOGO_PATHS[i]);
            ImageIcon icon = imageUrl != null ? new ImageIcon(imageUrl) : new ImageIcon();
            JButton imageButton = createDBMSButton(icon);

            imageButton.addActionListener(e -> {
                dbFormPanel.removeAll();
                dbFormPanel.add(getDbFormPanel(dbms), BorderLayout.CENTER);
                dbFormPanel.revalidate();
                dbFormPanel.repaint();
                cardLayout.show(cardPanel, "DBMSForm");
            });

            imageButton.addMouseListener(new java.awt.event.MouseAdapter() {
                Timer enlargeTimer, shrinkTimer;
                int currentSize = 80;

                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    if (shrinkTimer != null && shrinkTimer.isRunning()) shrinkTimer.stop();
                    enlargeTimer = new Timer(20, null);
                    enlargeTimer.addActionListener(e -> {
                        if (currentSize < 85) {
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
                        if (currentSize > 80) {
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

        backBtn = createStyledButton(Language.get("launcher.back"));
        backBtn.addActionListener(e -> cardLayout.show(cardPanel, "Launcher"));

        JPanel southPanel = new JPanel();
        southPanel.add(backBtn);

        panel.add(imagePanel, BorderLayout.CENTER);
        panel.add(southPanel, BorderLayout.SOUTH);

        return panel;
    }

    public JPanel getDbFormPanel(String dbms) {
        JPanel DBpanel = new JPanel();
        DBpanel.setLayout(new BoxLayout(DBpanel, BoxLayout.Y_AXIS));
        DBpanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        int imgIndex = switch (dbms) {
            case "MySQL" -> 0;
            case "PostgreSQL" -> 1;
            case "SQLServer" -> 2;
            case "SQLite" -> 3;
            default -> throw new IllegalStateException("Unexpected DBMS: " + dbms);
        };

        URL imageUrl = getClass().getResource(LOGO_PATHS[imgIndex]);
        ImageIcon icon = imageUrl != null ? new ImageIcon(imageUrl) : new ImageIcon();
        JButton imageButton = new JButton(new ImageIcon(icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
        imageButton.setPreferredSize(new Dimension(85, 85));
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

        JButton browseButton = createStyledButton(Language.get("launcher.browse"));
        JButton saveBtn = createStyledButton(Language.get("launcher.save_configuration"));
        JButton launchBtn = createStyledButton(Language.get("launcher.launch_without_saving"));
        JButton backBtn = createStyledButton(Language.get("launcher.back"));


        if (!isSQLite) {
            hostField = addFormRow(Language.get("launcher.host"), "localhost", DBpanel);
            portField = addFormRow(Language.get("launcher.port"), getDefaultPort(dbms), DBpanel);
            dbField = addFormRow(Language.get("launcher.database_name"), "warehouse_db", DBpanel);
            userField = addFormRow(Language.get("launcher.username"), "root", DBpanel);
            passField = addPasswordRow(DBpanel);
            saveBtn.addActionListener(e -> {
                ConfigManager.fillConfig(dbms, hostField.getText(), portField.getText(), dbField.getText(), userField.getText(), passField.getText());
                ConfigManager.saveConfig(dbms, hostField.getText(), portField.getText(), dbField.getText(), userField.getText(), passField.getText());
                showError(statusLabel, "Configuration saved for " + dbms);
            });

            launchBtn.addActionListener(e -> {
                ConfigManager.fillConfig(dbms, hostField.getText(), portField.getText(), dbField.getText(), userField.getText(), passField.getText());
                try {
                    launchApp();
                } catch (Exception ex) {
                    showError(statusLabel, "Failed to launch application. Check connection.");
                }
            });
        } else {

            dbField = addFormRow(Language.get("launcher.database_file"), "warehouse_db.sqlite", DBpanel);
            saveBtn.addActionListener(e -> {
                String dbPath = dbField.getText();
                File sqliteFile = new File(dbPath);
                if (!sqliteFile.exists()) {
                    showError(statusLabel, "SQLite file does not exist: " + dbPath);
                    return;
                }
                ConfigManager.fillConfig(dbms, null, null, dbField.getText(), null, null);
                ConfigManager.saveConfig(dbms, null, null, dbField.getText(), null, null);
                showError(statusLabel, "Configuration saved for " + dbms);
            });

            launchBtn.addActionListener(e -> {
                ConfigManager.fillConfig(dbms, null, null, dbField.getText(), null, null);
                String dbPath = ConfigManager.getConfig().dbName;
                File sqliteFile = new File(dbPath);
                if (!sqliteFile.exists()) {
                    showError(statusLabel, "SQLite file does not exist: " + dbPath);
                    return;
                }
                try {
                    launchApp();
                } catch (Exception ex) {
                    showError(statusLabel, "Failed to launch application. Check connection.");
                }
            });

            browseButton.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Select SQLite Database File");
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    dbField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            });

            DBpanel.add(Box.createVerticalStrut(10));
            DBpanel.add(browseButton);
        }



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

    private JButton createDBMSButton(ImageIcon icon) {
        Image scaled = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        JButton button = new JButton(new ImageIcon(scaled));
        button.setPreferredSize(new Dimension(85, 85));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        return button;
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

        JLabel jLabel = new JLabel(Language.get("launcher.password"));
        jLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        jLabel.setPreferredSize(new Dimension(120, 25));

        JPasswordField field = new JPasswordField("password");
        field.setPreferredSize(new Dimension(200, 30));

        gbc.gridx = 0;
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

    private void toggleFullscreen(JButton button) {
        isFullscreen = !isFullscreen;
        reloadTexts();
    }

    private void toggleLanguage() {
        String current = Language.getCurrentLanguage();
        String newLang = current.equals("en") ? "tr" : "en";
        Session.getInstance().setLanguage(newLang); // this also triggers reloadTexts()
    }


    private void showError(JLabel statusLabel, String message) {
        statusLabel.setText(message);
        new Timer(3000, evt -> {
            statusLabel.setText(" ");
            ((Timer) evt.getSource()).stop();
        }).start();
    }

    private void launchApp() {
        Database.connect();
        dispose();
        Session.getInstance().setFullscreen(isFullscreen);
        new App();
    }



    private String getDefaultPort(String dbms) {
        return switch (dbms) {
            case "MySQL" -> "3306";
            case "PostgreSQL" -> "5432";
            case "SQLServer" -> "1433";
            default -> "";
        };
    }

    private void reloadTexts() {
        langBtn.setText(Language.get("launcher.language"));
        fullscreenBtn.setText(Language.get("launcher.fullscreen") +": "+ (isFullscreen ? Language.get("launcher.enabled") : Language.get("launcher.disabled")));
        configBtn.setText(Language.get("launcher.configure_database"));
        startBtn.setText(Language.get("launcher.start_application"));
        backBtn.setText(Language.get("launcher.back"));
    }


}