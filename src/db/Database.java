package db;

import logic.ConfigManager;
import logic.Session;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class Database {
    private static Connection connection;


    public static void connect()
            throws SQLException, ClassNotFoundException {
        ConfigManager.DBConfig config = ConfigManager.getConfig();

        String dbms = config.dbms;
        String host = config.host;
        String port = config.port;
        String dbName = config.dbName;
        String username = config.username;
        String password = config.password;

        if (connection == null || connection.isClosed()) {
            String dbmsNormalized = dbms.trim().toLowerCase();
            String url = switch (dbmsNormalized) {
                case "postgresql" -> {
                    Class.forName("org.postgresql.Driver");
                    yield "jdbc:postgresql://" + host + ":" + port + "/" + dbName;
                }
                case "sqlserver" -> {
                    Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                    yield "jdbc:sqlserver://" + host + ":" + port + ";databaseName=" + dbName;
                }
                case "mysql" -> {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    yield "jdbc:mysql://" + host + ":" + port + "/" + dbName;
                }
                case "sqlite" -> {
                    Class.forName("org.sqlite.JDBC");
                    yield "jdbc:sqlite:" + dbName; // dbName is the path to the .db file
                }
                default -> throw new IllegalArgumentException("Unsupported DBMS type: " + dbms);
            };

            System.out.println(dbms);

            if ("sqlite".equals(dbmsNormalized)) {
                connection = DriverManager.getConnection(url);
            } else {
                connection = DriverManager.getConnection(url, username, password);
            }

            Session.getInstance().setDBMS(dbms);
        }
    }



    public static Connection getConnection() {
        return connection;
    }

    public static void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            System.out.println("Connection closed");
            connection.close();
        }
    }
}
