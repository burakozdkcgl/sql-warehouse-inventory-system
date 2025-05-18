package gui;

import logic.App;
import logic.Language;
import logic.Session;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class NavigationPanel extends JPanel {

    public NavigationPanel(JPanel contentPanel) {
        setLayout(new OverlayLayout(this));
        setOpaque(false);

        // Add content first (background), then the nav bar (on top)
        add(createNavBar());
        add(contentPanel);
    }

    private JPanel createNavBar() {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setOpaque(false);

        // Push nav bar to the bottom
        wrapper.add(Box.createVerticalGlue());

        // Container for the nav bar with background
        JPanel navContainer = new JPanel();
        navContainer.setOpaque(true);
        navContainer.setBackground(Color.WHITE);
        navContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        navContainer.setMaximumSize(new Dimension(920, 60)); // This keeps the nav bar narrow
        navContainer.setAlignmentX(Component.CENTER_ALIGNMENT); // Center it horizontally

        navContainer.add(createNavButton(Language.get("nav.dashboard"), () ->
                App.getInstance().setScreen(new MainPanel())));

        if  (Objects.equals(Session.getInstance().getCurrentUser().getRole(), "admin") ||
                Objects.equals(Session.getInstance().getCurrentUser().getRole(), "manager")
        ){
            navContainer.add(createNavButton(Language.get("nav.manage"), () ->
                    App.getInstance().setScreen(new ManagePanel())));
        }

        if  (Objects.equals(Session.getInstance().getCurrentUser().getRole(), "admin") ||
                Objects.equals(Session.getInstance().getCurrentUser().getRole(), "manager") ||
            Objects.equals(Session.getInstance().getCurrentUser().getRole(), "employee")
        ){
            navContainer.add(createNavButton(Language.get("nav.orders"), () ->
                    App.getInstance().setScreen(new OrderPanel())));
        }



        navContainer.add(createNavButton(Language.get("nav.inventory"), () ->
                App.getInstance().setScreen(new InventoryPanel())));

        navContainer.add(createNavButton(Language.get("nav.item_list"), () ->
                App.getInstance().setScreen(new ItemListPanel())));

        navContainer.add(createNavButton(Language.get("nav.user_list"), () ->
                App.getInstance().setScreen(new UserListPanel())));

        navContainer.add(createNavButton(Language.get("nav.settings"), () ->
                App.getInstance().setScreen(new SettingsPanel())));


        wrapper.add(Box.createVerticalStrut(10)); // spacing from bottom
        wrapper.add(navContainer);
        wrapper.add(Box.createVerticalStrut(10)); // spacing if needed

        return wrapper;
    }


    private JButton createNavButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(new Color(230, 230, 230));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(110, 35));
        button.addActionListener(e -> action.run());

        return button;
    }
}
