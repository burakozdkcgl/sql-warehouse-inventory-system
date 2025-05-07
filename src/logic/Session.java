package logic;

import entity.User;

public class Session {

    private static Session instance;
    private User currentUser;
    private boolean isFullscreen;

    public static Session getInstance() {
        return instance;
    }

    public Session() {
        instance = this;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setFullscreen(boolean isFullscreen){
        this.isFullscreen = isFullscreen;
    }

    public boolean isFullscreen(){
        return isFullscreen;
    }

}
