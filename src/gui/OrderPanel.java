package gui;

import entity.User;
import logic.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class OrderPanel extends JPanel {


    public OrderPanel() {
        setLayout(new GridBagLayout());
        setOpaque(false);

        User user = Session.getInstance().getCurrentUser();
        if (user == null) return;

        JPanel whiteBox = new JPanel();
        whiteBox.setLayout(new BorderLayout(10, 10));
        whiteBox.setBackground(Color.WHITE);
        whiteBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(20, 20, 20, 20)
        ));
        whiteBox.setPreferredSize(new Dimension(950, 620));

        // Header with user welcome
        JLabel welcomeLabel = new JLabel("OrderPanel panel", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        whiteBox.add(welcomeLabel, BorderLayout.NORTH);





        add(whiteBox);
    }
}
