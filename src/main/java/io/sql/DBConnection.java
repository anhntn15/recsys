package io.sql;

import config.Resources;

import java.sql.Connection;
import java.sql.DriverManager;


public class DBConnection {
    private static DBConnection instance;
    private Connection con;

    private DBConnection() {
        Resources resources = Resources.getInstance();
        String host = resources.getProperty("database.host");
        String port = resources.getProperty("database.port");
        String dbName = resources.getProperty("database.name");
        String dbUser = resources.getProperty("database.user");
        String dbPassword = resources.getProperty("database.pass");

        try {
            String url = String.format("jdbc:sqlserver://%s:%s;user=%s;password=%s;",
                    host, port, dbUser, dbPassword);
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = DriverManager.getConnection(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection createConnection() {
        Resources resources = Resources.getInstance();
        String host = resources.getProperty("database.host");
        String port = resources.getProperty("database.port");
        String dbName = resources.getProperty("database.name");
        String dbUser = resources.getProperty("database.user");
        String dbPassword = resources.getProperty("database.pass");

        try {
            String url = String.format("jdbc:sqlserver://%s:%s;user=%s;password=%s;",
                    host, port, dbUser, dbPassword);
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static DBConnection getInstance() {
        if (instance == null) {
            synchronized (DBConnection.class) {
                if (instance == null)
                    instance = new DBConnection();
            }
        }

        return instance;
    }

    public Connection getConnection() {
        return con;
    }
}
