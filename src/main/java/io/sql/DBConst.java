package io.sql;

import config.Resources;

/**
 * Constants for database
 * TODO load config from file
 */
public class DBConst {
    public static final String LOG_TABLE = Resources.getInstance().getProperty("database.user_log_table");
    public static final String ITEM_TABLE = Resources.getInstance().getProperty("database.item_table");
}
