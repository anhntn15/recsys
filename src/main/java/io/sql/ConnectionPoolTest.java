package io.sql;

import com.microsoft.sqlserver.jdbc.SQLServerConnectionPoolDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import config.Resources;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectionPoolTest {
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
        return poolDataSource;
    }

    public static Connection getConnection(){
//        poolDataSource.
        try {
            return poolDataSource.getConnection();
        } catch (SQLServerException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void main(String[] args) throws SQLException {
        SQLServerConnectionPoolDataSource poolDataSource = new SQLServerConnectionPoolDataSource();
//        poolDataSource.

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
//        poolDataSource.set
        PooledConnection s = poolDataSource.getPooledConnection();

        Connection connection = s.getConnection();

        String sql = "SELECT Top (1000) Id, MinAcreage, MaxAcreage, CityId, DistrictId, MinPrice, MaxPrice, BedRoom, SellType, CategoryId " +
                " FROM HDRecommend.dbo.Product";


        PreparedStatement ps = null;
        ResultSet rs = null;
        ps = connection.prepareStatement(sql);
        rs = ps.executeQuery();
        while (rs.next()) {
            System.out.println(rs.getInt(1));
        }

        poolDataSource.getPooledConnection().close();
    }
}
