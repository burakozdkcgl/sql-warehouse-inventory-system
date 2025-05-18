package logic;

import entity.User;

public class Session {

    private static Session instance;
    private User currentUser;
    private String language;
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

    public void setLanguage(String language){
        this.language = language;
        Language.load(language);
    }

    public String getLanguage(){return language;}
}
