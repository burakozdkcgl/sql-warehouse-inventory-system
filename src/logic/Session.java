package logic;

public class Session {

    private static Session instance;
    private model.User currentUser;
    private String DBMS;
    private boolean isFullscreen;

    public static Session getInstance() {
        return instance;
    }

    public Session() {
        instance = this;
    }

    public void setCurrentUser(model.User user) {
        this.currentUser = user;
    }

    public model.User getCurrentUser() {
        return currentUser;
    }

    public void setDBMS(String dbms) {
        this.DBMS = dbms;
    }

    public String getDBMS() {
        return DBMS;
    }

    public void setFullscreen(boolean turufols){
        this.isFullscreen = turufols;
    }

    public boolean isFullscreen(){
        return isFullscreen;
    }

}
