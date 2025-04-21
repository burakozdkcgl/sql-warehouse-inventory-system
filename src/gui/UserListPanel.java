package gui;

import db.Database;
import entity.User;
import logic.App;
import org.hibernate.Session;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserListPanel extends JPanel {

    public UserListPanel() {
        setLayout(new GridBagLayout());
        setOpaque(false);

        JPanel whiteBox = new JPanel(new BorderLayout(10, 10));
        whiteBox.setBackground(Color.WHITE);
        whiteBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(20, 20, 20, 20)
        ));
        whiteBox.setPreferredSize(new Dimension(950, 620));

        JLabel title = new JLabel("User List", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(40, 40, 40));
        whiteBox.add(title, BorderLayout.NORTH);

        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setOpaque(false);

        try (Session session = Database.getSessionFactory().openSession()) {
            List<User> users = session.createQuery("FROM User", User.class).list();
            Map<String, List<User>> grouped = users.stream().collect(Collectors.groupingBy(User::getRole));

            List<String> priorityRoles = List.of("admin", "manager", "employee", "auditor");

            for (String role : priorityRoles) {
                if (!grouped.containsKey(role)) continue;
                addRolePanel(role, grouped.get(role), userPanel);
            }

            for (String role : grouped.keySet()) {
                if (priorityRoles.contains(role)) continue;
                addRolePanel(role, grouped.get(role), userPanel);
            }

        } catch (Exception e) {
            logic.ErrorLogger.log(e);
            userPanel.add(new JLabel("Failed to load users."));
        }

        JScrollPane scrollPane = new JScrollPane(userPanel);
        scrollPane.setBorder(null);
        whiteBox.add(scrollPane, BorderLayout.CENTER);
        whiteBox.add(Box.createVerticalStrut(20), BorderLayout.SOUTH);

        add(whiteBox);
    }

    private void addRolePanel(String role, List<User> roleUsers, JPanel userPanel) {
        JPanel rolePanel = new JPanel();
        rolePanel.setLayout(new BoxLayout(rolePanel, BoxLayout.Y_AXIS));
        rolePanel.setBackground(new Color(250, 250, 250));
        rolePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel roleLabel = new JLabel(role.substring(0, 1).toUpperCase() + role.substring(1));
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        roleLabel.setForeground(new Color(60, 60, 60));
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        roleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        roleLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, roleLabel.getPreferredSize().height));

        JPanel labelWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        labelWrapper.setOpaque(false);
        labelWrapper.add(roleLabel);
        rolePanel.add(labelWrapper);
        rolePanel.add(Box.createVerticalStrut(10));

        JPanel gridPanel = new JPanel(new GridBagLayout());
        gridPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        for (int i = 0; i < roleUsers.size(); i++) {
            JPanel card = createUserCard(roleUsers.get(i));
            gbc.gridx = i % 2;
            gbc.gridy = i / 2;
            gridPanel.add(card, gbc);
        }

        if (roleUsers.size() == 1) {
            JPanel invisibleCard = createUserCard(roleUsers.get(0));
            invisibleCard.setOpaque(false);
            invisibleCard.setBorder(null);
            for (Component comp : invisibleCard.getComponents()) {
                comp.setVisible(false);
            }
            gbc.gridx = 1;
            gbc.gridy = 0;
            gridPanel.add(invisibleCard, gbc);
        }

        rolePanel.add(gridPanel);
        rolePanel.add(Box.createVerticalStrut(10));
        userPanel.add(rolePanel);
        userPanel.add(Box.createVerticalStrut(20));
    }

    private JPanel createUserCard(User user) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(10, 10));
        card.setBackground(new Color(245, 245, 245));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(10, 10, 10, 10)
        ));
        card.setPreferredSize(new Dimension(420, 100));

        JLabel avatarLabel = new JLabel(new ImageIcon(getAvatarImage(user)));
        avatarLabel.setPreferredSize(new Dimension(80, 80));
        card.add(avatarLabel, BorderLayout.WEST);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        info.add(new JLabel("Name: " + user.getName()));
        info.add(new JLabel("Username: " + user.getUsername()));
        if (user.getManager() != null)
            info.add(new JLabel("Manager: " + user.getManager().getName()));
        card.add(info, BorderLayout.CENTER);

        JButton visitButton = new JButton("Visit Profile");
        visitButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        visitButton.setBackground(new Color(70, 130, 180));
        visitButton.setForeground(Color.WHITE);
        visitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        visitButton.addActionListener(e -> App.getInstance().setScreen(new UserProfilePanel(user)));

        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonWrapper.setOpaque(false);
        buttonWrapper.add(visitButton);
        card.add(buttonWrapper, BorderLayout.EAST);

        return card;
    }



    private Image getAvatarImage(User user) {
        byte[] imgBytes = user.getPicture();
        try {
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(imgBytes));
            return img.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            return new BufferedImage(80, 80, BufferedImage.TYPE_INT_ARGB);
        }
    }
}
