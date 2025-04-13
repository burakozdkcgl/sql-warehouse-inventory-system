package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Auth {

    public static boolean attemptLogin(String id, String password) {
        try {
            Connection conn = Database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM users WHERE id = ? AND password = ?"
            );
            stmt.setString(1, id);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            boolean success = rs.next();
            rs.close();
            stmt.close();
            return success;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
