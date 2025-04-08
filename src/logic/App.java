package logic;
import gui.LoginPanel;

import javax.swing.*;
import java.awt.*;

public class App extends JFrame {
    private final JPanel root = new JPanel(new BorderLayout());
    private static App instance;
    private model.User currentUser;

    public static App getInstance() {
        return instance;
    }

    public void setCurrentUser(model.User user) {
        this.currentUser = user;
    }

    public model.User getCurrentUser() {
        return currentUser;
    }

    public App() {
        instance = this;
        setTitle("Warehouse Inventory System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setContentPane(root);
        setSize(1200, 800);
        setLocationRelativeTo(null); // centers on screen
        setVisible(true);
        setScreen(new LoginPanel());
    }

    public void setScreen(JPanel screen) {
        root.removeAll();
        root.add(screen);
        screen.validate();
        root.revalidate();
        root.repaint();
    }





}