package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class Database {
    private static Connection connection;



    public static void connect(String dbms, String host, String port, String dbName, String username, String password)
            throws SQLException, ClassNotFoundException {

        System.out.println("Attempting to load driver: " + dbms);

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

            System.out.println(url);
            if ("sqlite".equals(dbmsNormalized)) {
                connection = DriverManager.getConnection(url);
            } else {
                connection = DriverManager.getConnection(url, username, password);
            }
        }

        System.out.println("Connection successful");
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
