package logic;

public class Auth {

    public static boolean isLoginValid(String id, String password){
        if ((id.equals("admin") && password.equals("admin")) || (id.equals("staff") && password.equals("staff"))) {
            return true;
        }
        else return false;
    }
}
