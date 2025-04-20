import db.Auth;
import db.Database;
import entity.User;
import logic.App;
import logic.Session;
import logic.ConfigManager;

import javax.swing.*;

public class Test {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new Session();
                ConfigManager.loadConfig();
                Database.connect();

                User testUser;
                try (org.hibernate.Session session = Database.getSessionFactory().openSession()) {
                    testUser = session.get(User.class, 2);
                }


                Session.getInstance().setFullscreen(false);
                Session.getInstance().setCurrentUser(testUser);

                Session.getInstance().setCurrentUser(testUser);
                // Right after this, force reload using Auth
                User loginUser = Auth.attemptLogin(testUser.getUsername(), testUser.getPassword());
                Session.getInstance().setCurrentUser(loginUser);

                App app = new App();

                // Hijack splash transition
                new java.util.Timer().schedule(new java.util.TimerTask() {
                    @Override
                    public void run() {
                        SwingUtilities.invokeLater(() -> {
                            app.setScreen(new gui.MainPanel());
                        });
                    }
                }, 4500);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
