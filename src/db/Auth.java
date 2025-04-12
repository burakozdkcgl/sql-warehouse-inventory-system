package db;

public class Auth {

    public static boolean attemptLogin(String id, String password){
        if ((id.equals("admin") && password.equals("admin")) || (id.equals("staff") && password.equals("staff"))) {
            return true;
        }
        else return false;
    }
}
