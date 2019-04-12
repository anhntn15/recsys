package io.sql;

import com.microsoft.sqlserver.jdbc.SQLServerConnectionPoolDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerXADataSource;
import config.Resources;

import javax.sql.PooledConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionPoolXA {
    private static final SQLServerConnectionPoolDataSource poolDataSource = init();

    private static SQLServerConnectionPoolDataSource init(){
        SQLServerConnectionPoolDataSource poolDataSource = new SQLServerConnectionPoolDataSource();
        Resources resources = Resources.getInstance();
        String host = resources.getProperty("database.host");
        String port = resources.getProperty("database.port");
        String dbName = resources.getProperty("database.name");
        String dbUser = resources.getProperty("database.user");
        String dbPassword = resources.getProperty("database.pass");
        poolDataSource.setServerName(host);
        poolDataSource.setDatabaseName(dbName);
        poolDataSource.setUser(dbUser);
        poolDataSource.setPassword(dbPassword);
        SQLServerXADataSource XADataSource1 = new SQLServerXADataSource();
        XADataSource1.setServerName(host);
        XADataSource1.setDatabaseName(dbName);
        XADataSource1.setUser(dbUser);
        XADataSource1.setPassword(dbPassword);
        return XADataSource1;
    }

    public static PooledConnection getPoolConnection(){
        try {
            return poolDataSource.getPooledConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
