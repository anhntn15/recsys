package io.sql;

import datastruct.HistoryCell;
import datastruct.Item;
import datastruct.feature.*;
import io.LogObject;
import org.apache.log4j.Logger;

import javax.sql.PooledConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/***
 * Class to manage access user history from database
 */
public class UserLogDBHandler {
    private static final Logger LOGGER = Logger.getLogger(UserLogDBHandler.class);
    private Connection con;

    public UserLogDBHandler() {
        con = DBConnection.getInstance().getConnection();
    }

    /**
     * Update list of log to database
     *
     * @param logs
     * @return
     */
    public boolean insertLog(List<LogObject> logs) {
        if (logs.size() == 0)
            return true;

        PreparedStatement ps = null;
        String sql = "IF NOT EXISTS ( SELECT id from " + DBConst.LOG_TABLE + " WHERE id = ?) " +
                "BEGIN INSERT INTO " + DBConst.LOG_TABLE + " (id, user_id, item_id, time_on_site, click_to_call, timestamp, update_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP) END;";
        boolean ok = true;

        try {
            ps = con.prepareStatement(sql);
            for (int i = 0; i < logs.size(); i++) {
                ps.setString(1, logs.get(i).getId());
                ps.setString(2, logs.get(i).getId());
                ps.setString(3, logs.get(i).getUid());
                ps.setLong(4, logs.get(i).getItemId());
                ps.setInt(5, logs.get(i).getTimeOnSite());
                ps.setInt(6, logs.get(i).getClickToCall());
                ps.setTimestamp(7, new Timestamp(logs.get(i).getCreateTime().getTime()));
                ps.addBatch();
            }

            int result[] = ps.executeBatch();
            for (int i : result) {
                if (i < 0) {
                    ok = false;
                    break;
                }
            }
        } catch (SQLException e) {
            LOGGER.error("SQL Error", e);
//            e.printStackTrace();
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return ok;
    }

    /**
     * Delete old log. Delete logs which have timestamp before <param>timeStampThresh</param>
     *
     * @param timeStampThresh Date to determine log have been deleted
     * @return num of log have been deleted
     */
    public static int deleteOldLogs(java.util.Date timeStampThresh) {
        Connection con = null;
        PreparedStatement ps = null;
        String sql = "DELETE from " + DBConst.LOG_TABLE + " WHERE timestamp < ?;";
        int ok = 0;

        try {
            con = DBConnection.createConnection();
            if (con == null) {
                return 0;
            }
            ps = con.prepareStatement(sql);
            ps.setTimestamp(1, new Timestamp(timeStampThresh.getTime()));
            ok = ps.executeUpdate();
            con.commit();
        } catch (SQLException e) {
            LOGGER.error("SQL Error", e);
//            e.printStackTrace();
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return ok;
    }

    /**
     * Get list Log user read
     *
     * @param uid user id
     * @return list Log
     */
    public List<LogObject> getLogItemIdByUser(String uid) {
        Connection con = ConnectionPoolTest.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT Top (50) * FROM " + DBConst.LOG_TABLE + " WHERE user_id = '" + uid + "'  ORDER BY timestamp desc";
        List<LogObject> logs = new ArrayList<>();
        try {
            if (con == null) {
                return null;
            }
            ps = con.prepareStatement(sql);
            LOGGER.info(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                logs.add(new LogObject(rs.getString("id"), rs.getString("user_id"), rs.getLong("item_id"), rs.getDate("timestamp")));
            }
        } catch (SQLException e) {
            LOGGER.error("SQL Error", e);
//            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return logs;
    }

    /**
     * Get list Log user read
     *
     * @param startDate start date
     * @return list Log
     */
    public static List<LogObject> getNewLog(Date startDate, int limit) {
        Connection con = ConnectionPoolTest.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT Top (?) * FROM " + DBConst.LOG_TABLE + " WHERE update_time >= ?  ORDER BY update_time ";
        List<LogObject> logs = new ArrayList<>();
        try {
            if (con == null) {
                return null;
            }
            ps = con.prepareStatement(sql);
            ps.setInt(1, limit);
            ps.setTimestamp(2, new Timestamp(startDate.getTime()));
            LOGGER.info(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                logs.add(new LogObject(rs.getString("id"), rs.getString("user_id"),
                        rs.getLong("item_id"), rs.getTimestamp("timestamp"),
                        rs.getTimestamp("update_time")));
            }
        } catch (SQLException e) {
            LOGGER.error("SQL Error", e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return logs;
    }

    /**
     * Get list Log user read
     *
     * @param startId start date
     * @return list Log
     */
    public static List<LogObject> getNewLog(String startId, int limit) {
        Connection con = ConnectionPoolTest.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT Top (?) * FROM " + DBConst.LOG_TABLE + " WHERE id > ?  ORDER BY id ";
        List<LogObject> logs = new ArrayList<>();
        try {
            if (con == null) {
                return null;
            }
            ps = con.prepareStatement(sql);
            ps.setInt(1, limit);
            ps.setString(2, startId);
            LOGGER.info(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                logs.add(new LogObject(rs.getString("id"), rs.getString("user_id"),
                        rs.getLong("item_id"), rs.getTimestamp("timestamp"),
                        rs.getTimestamp("update_time")));
            }
        } catch (SQLException e) {
            LOGGER.error("SQL Error", e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return logs;
    }

    public List<HistoryCell> getLogItemByUser(String uid) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT Top (50) I.Id, I.SellType, I.MinPrice, I.MaxPrice, I.MinAcreage, I.MaxAcreage, " +
                " I.CategoryID, I.CityID, I.DistrictID, I.BedRoom, I.Pretokenized, I.Latitude, I.Longitude, L.user_id, " +
                "L.time_on_site, L.click_to_call, L.timestamp" +
                " FROM " + DBConst.LOG_TABLE + " L " +
                " INNER JOIN " + DBConst.ITEM_TABLE + " I ON L.item_id = I.Id " +
                " WHERE L.user_id = ? " +
                " ORDER BY L.timestamp desc";

        List<HistoryCell> logs = new ArrayList<>();
        PooledConnection pc = ConnectionPoolXA.getPoolConnection();
        if (pc == null) {
            LOGGER.warn("Get connection SQL fail");
            return null;
        }
        try {
            con = pc.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, uid);
            rs = ps.executeQuery();
            while (rs.next()) {
                double lat = rs.getDouble("Latitude");
                double lon = rs.getDouble("Longitude");
                Point p = null;
                if (lat > 0.0 && lon > 0.0)
                    p = new Point(lat, lon);

                Item item = new Item(rs.getLong("Id"),
                        new Acreage(rs.getDouble("MinAcreage"), rs.getDouble("MaxAcreage")),
                        new Location(new Address(rs.getInt("CityId"), rs.getInt("DistrictId")), p),
                        new Price(rs.getDouble("MinPrice"), rs.getDouble("MaxPrice")),
                        new Content(rs.getString("Pretokenized")),
                        new RoomNumber(rs.getInt("BedRoom")),
                        rs.getInt("SellType"),
                        rs.getInt("CategoryId")
                );
                logs.add(new HistoryCell(item, rs.getInt("time_on_site"), rs.getInt("click_to_call") == 1, rs.getDate("timestamp")));
            }
        } catch (SQLException e) {
            LOGGER.error("SQL Error", e);
//            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return logs;
    }

    public List<String> getUniqueUserIds() {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<String> list = new ArrayList<>();
        String sql = "SELECT user_id FROM " + DBConst.LOG_TABLE + " " +
                " GROUP BY user_id HAVING count(item_id) >= 20";
        try {
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("user_id"));
            }
        } catch (SQLException e) {
            LOGGER.error("SQL Error", e);
//            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return list;
    }
}
