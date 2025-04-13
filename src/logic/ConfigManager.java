package logic;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {

    private static final DBConfig config = new DBConfig();

    private static final boolean isSQLite = "SQLite".equalsIgnoreCase(config.dbms);

    public static void saveConfig(String dbms, String host, String port, String dbName, String user, String pass) {
        Properties props = new Properties();
        props.setProperty("dbms", dbms);
        props.setProperty("dbName", dbName);

        if (!isSQLite) {
            if (config.host != null) props.setProperty("host", host);
            if (config.port != null) props.setProperty("port", port);
            if (config.username != null) props.setProperty("username", user);
            if (config.password != null) props.setProperty("password", pass);
        } else {
            props.setProperty("host", "null");
            props.setProperty("port", "null");
            props.setProperty("username", "null");
            props.setProperty("password", "null");
        }

        try (FileOutputStream out = new FileOutputStream("config.properties")) {
            props.store(out, null);
        } catch (IOException e) {
            ErrorLogger.log(e);
        }
    }

    public static void loadConfig() {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream("config.properties")) {
            props.load(in);
            config.dbms = props.getProperty("dbms");
            config.host = props.getProperty("host");
            config.port = props.getProperty("port");
            config.dbName = props.getProperty("dbName");
            config.username = props.getProperty("username");
            config.password = props.getProperty("password");
        } catch (IOException e) {
            ErrorLogger.log(e);
        }
    }

    public static void fillConfig(String dbms, String host, String port, String dbName, String user, String pass) {
        config.dbms = dbms;
        config.dbName = dbName;
        if (isSQLite) {
            config.host = config.port = config.username = config.password = "null";
        } else {
            config.host = host;
            config.port = port;
            config.username = user;
            config.password = pass;
        }
    }

    public static DBConfig  getConfig(){
        return config;
    }

    public static String getDBMS(){
        loadConfig();
        return config.dbms;
    }
    public static class DBConfig {
        public String dbms, host, port, dbName, username, password;
    }
}