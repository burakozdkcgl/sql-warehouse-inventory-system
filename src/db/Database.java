package db;

import logic.ConfigManager;
import logic.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class Database {

    private static SessionFactory sessionFactory;

    public static void connect() {
        ConfigManager.DBConfig config = ConfigManager.getConfig();

        if (sessionFactory != null) return;

        Configuration cfg = new Configuration();

        String dbms = config.dbms.toLowerCase().trim();
        String dialect = switch (dbms) {
            case "mysql" -> "org.hibernate.dialect.MySQLDialect";
            case "postgresql" -> "org.hibernate.dialect.PostgreSQLDialect";
            case "sqlserver" -> "org.hibernate.dialect.SQLServerDialect";
            case "sqlite" -> "org.hibernate.community.dialect.SQLiteDialect";
            default -> throw new IllegalArgumentException("Unsupported DBMS: " + dbms);
        };

        cfg.setProperty("hibernate.connection.driver_class", getDriverClass(dbms));
        cfg.setProperty("hibernate.connection.url", getJdbcUrl(config));
        cfg.setProperty("hibernate.connection.username", config.username == null ? "" : config.username);
        cfg.setProperty("hibernate.connection.password", config.password == null ? "" : config.password);
        cfg.setProperty("hibernate.dialect", dialect);
        cfg.setProperty("hibernate.hbm2ddl.auto", "update"); // or validate/create
        cfg.setProperty("hibernate.show_sql", "true");

        // Register entity classes here:
        cfg.addAnnotatedClass(entity.User.class);
        cfg.addAnnotatedClass(entity.Item.class);
        cfg.addAnnotatedClass(entity.Warehouse.class);
        cfg.addAnnotatedClass(entity.Inventory.class);

        ServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySettings(cfg.getProperties())
                .build();

        sessionFactory = cfg.buildSessionFactory(registry);

        Session.getInstance().setDBMS(config.dbms);
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }



    public static void close() throws Exception {
        if (sessionFactory != null) sessionFactory.close();
    }

    private static String getDriverClass(String dbms) {
        return switch (dbms) {
            case "mysql" -> "com.mysql.cj.jdbc.Driver";
            case "postgresql" -> "org.postgresql.Driver";
            case "sqlserver" -> "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            case "sqlite" -> "org.sqlite.JDBC";
            default -> throw new IllegalArgumentException("Unsupported DBMS: " + dbms);
        };
    }

    private static String getJdbcUrl(ConfigManager.DBConfig config) {
        String dbms = config.dbms.toLowerCase().trim();
        return switch (dbms) {
            case "mysql" -> "jdbc:mysql://" + config.host + ":" + config.port + "/" + config.dbName;
            case "postgresql" -> "jdbc:postgresql://" + config.host + ":" + config.port + "/" + config.dbName;
            case "sqlserver" -> "jdbc:sqlserver://" + config.host + ":" + config.port + ";databaseName=" + config.dbName;
            case "sqlite" -> "jdbc:sqlite:" + config.dbName;
            default -> throw new IllegalArgumentException("Unsupported DBMS: " + dbms);
        };
    }
}
