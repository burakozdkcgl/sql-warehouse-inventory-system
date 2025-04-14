package db;

import entity.User;
import org.hibernate.Session;

public class Auth {

    public static User attemptLogin(String id, String password) {
        try (Session session = Database.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM User WHERE username = :id AND password = :pass", User.class)
                    .setParameter("id", id)
                    .setParameter("pass", password)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
