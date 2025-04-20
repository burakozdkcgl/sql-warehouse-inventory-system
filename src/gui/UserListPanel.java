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
            for (User user : users) {
                userPanel.add(createUserCard(user));
                userPanel.add(Box.createVerticalStrut(15));
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

    private JPanel createUserCard(User user) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(10, 10));
        card.setBackground(new Color(245, 245, 245));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(10, 10, 10, 10)
        ));
        card.setMaximumSize(new Dimension(900, 100));

        JLabel avatarLabel = new JLabel(new ImageIcon(getAvatarImage(user)));
        avatarLabel.setPreferredSize(new Dimension(80, 80));
        card.add(avatarLabel, BorderLayout.WEST);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        info.add(new JLabel("Name: " + user.getName()));
        info.add(new JLabel("Username: " + user.getUsername()));
        info.add(new JLabel("Role: " + user.getRole()));
        card.add(info, BorderLayout.CENTER);

        JButton visitButton = new JButton("Visit Profile");
        visitButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        visitButton.setBackground(new Color(220, 235, 255));
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
