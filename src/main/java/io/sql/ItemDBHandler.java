package io.sql;

import datastruct.Item;
import datastruct.feature.*;
import io.OriginItem;
import org.apache.log4j.Logger;

import javax.sql.PooledConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ItemDBHandler {
    private static final Logger LOGGER = Logger.getLogger(ItemDBHandler.class);

    public ItemDBHandler() {
    }

    /**
     * @param ids
     * @return
     */
    public List<Item> getItemByIdsMiniBatch(List<Long> ids) {
        int batchSize = 10000;
        int n = ids.size() / batchSize;
        List<Item> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            List<Long> tempIds = ids.subList(i * batchSize, (i + 1) * batchSize);
            result.addAll(getItemById(tempIds));
        }
        List<Long> tempIds = ids.subList(n * batchSize, ids.size());
        result.addAll(getItemById(tempIds));
        return result;
    }

    /**
     * @param ids
     * @return
     */
    public List<Item> getItemById(List<Long> ids) {
        StringBuilder builder = new StringBuilder();
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        builder.append(ids.get(0));
        for (int i = 1; i < ids.size(); i++) {
            builder.append(", ");
            builder.append(ids.get(i));
        }
        String sql = "SELECT Id, Pretokenized, Longitude, Latitude, MinAcreage, MaxAcreage, CityId, DistrictId, " +
                " MinPrice, MaxPrice, BedRoom, SellType, CategoryId " +
                " FROM " + DBConst.ITEM_TABLE + " WHERE id in (" + builder.toString() + ") AND Pretokenized is not NULL";
        return getListItemByQuery(sql);
    }

    public List<Item> getAllItemFromId(Long id) {
        String sql = "SELECT Id, Pretokenized, Longitude, Latitude, MinAcreage, MaxAcreage, CityId, DistrictId, " +
                " MinPrice, MaxPrice, BedRoom, SellType, CategoryId" +
                " FROM " + DBConst.ITEM_TABLE + "  WHERE id > " + id + " ORDER BY Id asc";
        return getListItemByQuery(sql);
    }

    private List<Item> getListItemByQuery(String query) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Item> items = new ArrayList<>();
        PooledConnection pc = ConnectionPoolXA.getPoolConnection();
        if (pc != null) {
            try {
                connection = pc.getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection == null) {
            return items;
        }
        try {
            ps = connection.prepareStatement(query);
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
                        rs.getInt("CategoryId"));

                items.add(item);
            }
        } catch (Exception e) {
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
            try {
                connection.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

        return items;
    }


    /**
     * Get item ids have end date larger than <param>data</param>
     *
     * @param date
     * @return
     */
    public List<Long> getIdAvailable(Date date) {
        String query = "SELECT Id FROM " + DBConst.ITEM_TABLE + "  WHERE EndDate >= ? AND Pretokenized is not NULL";

        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Long> ids = new ArrayList<>();
        Connection connection = null;
        try {
            connection = ConnectionPoolTest.getConnection();
            if (connection == null) {
                return ids;
            }
            ps = connection.prepareStatement(query);
            ps.setTimestamp(1, new Timestamp(date.getTime()));
            rs = ps.executeQuery();
            while (rs.next()) {
                ids.add(rs.getLong("Id"));
            }
        } catch (Exception e) {
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
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return ids;
    }

    public List<OriginItem> getItemNonTokenize(int limit) {
        String query = "SELECT TOP (" + limit + ") Id, Name ,Description" +
                " FROM " + DBConst.ITEM_TABLE + "  WHERE Pretokenized IS NULL";

        PreparedStatement ps = null;
        ResultSet rs = null;
        List<OriginItem> items = new ArrayList<>();
        PooledConnection pc = ConnectionPoolXA.getPoolConnection();
        Connection connection = null;
        if (pc != null) {
            try {
                connection = pc.getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection == null) {
            return new ArrayList<>();
        }
        try {
            ps = connection.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                OriginItem originItem = new OriginItem(rs.getLong("Id"), rs.getString("Name"),
                        rs.getString("Description"));
                items.add(originItem);
            }
        } catch (Exception e) {
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
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return items;
    }

    public void updateTokenizes(Map<Long, String> tokens) {
        String query = "UPDATE " + DBConst.ITEM_TABLE + " SET Pretokenized = ? WHERE Id = ?;";
        Connection connection = null;
        PreparedStatement ps = null;
        PooledConnection pc = ConnectionPoolXA.getPoolConnection();
        if (pc != null) {
            try {
                connection = pc.getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection == null) {
            return;
        }
        try {
            ps = connection.prepareStatement(query);
            for (Map.Entry<Long, String> entry : tokens.entrySet()) {
                ps.setString(1, entry.getValue());
                ps.setLong(2, entry.getKey());
                ps.addBatch();
            }
            ps.executeBatch();
            connection.commit();
        } catch (Exception e) {
            LOGGER.error("SQL Error", e);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }
}
