package datamanager;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import datastruct.HistoryCell;
import io.sql.UserLogDBHandler;
import multithread.ReloadProcess;
import multithread.Reloadable;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class UserLogManagerBackup implements Reloadable {
    private static final Logger LOGGER = Logger.getLogger(UserLogManagerBackup.class);
    private static final long RELOAD_CIRCLE_TIME = 60 * 1000L;
    //    private Map<String, List<HistoryCell>> userHistories;
    private Cache<String, List<HistoryCell>> cacheHistory;
    private Map<String, Date> userLastLogTime;
    private UserLogDBHandler logReader;
    private List<String> uniqueUserId;
    private static UserLogManagerBackup instance = new UserLogManagerBackup();
    private static int random;

    private UserLogManagerBackup() {
//        userHistories = new ConcurrentHashMap<>();
        logReader = new UserLogDBHandler();
        uniqueUserId = logReader.getUniqueUserIds();
        System.out.println("uniqueUserId: " + uniqueUserId.size());
        random = uniqueUserId.size();
        userLastLogTime = new ConcurrentHashMap<>();
        this.cacheHistory = CacheBuilder.newBuilder().expireAfterAccess(3, TimeUnit.DAYS).build();
    }

    public static UserLogManagerBackup getInstance() {
        return instance;
    }

//    public void updateLog4User(String uid, Item... newLogs) {
//        List<Item> items = userHistories.containsKey(uid) ? userHistories.get(uid) : new ArrayList<>();
//        items.addAll(Arrays.asList(newLogs));
//        userHistories.put(uid, items);
//    }

    private List<HistoryCell> loadUserHistory(String userID) {
//        List<LogObject> dbLogs = logReader.getLogItemIdByUser(userID);
//        if(dbLogs == null || dbLogs.size() == 0) {
//            return;
//        }
//        ItemManager itemManager = ItemManager.getInstance();
//        List<HistoryCell> logs = new ArrayList<>();
//        for(LogObject dbObject: dbLogs){
//            Item item = itemManager.getItem(dbObject.getItemId());
//            HistoryCell historyCell = new HistoryCell(item, dbObject.getTimeOnSite(), dbObject.isClickToCall());
//            logs.add(historyCell);
//        }
//        userHistories.put(userID, logs);
//        userLastLogTime.put(userID, logs.get(0).getItem().getCreateTime());
        List<HistoryCell> logs = logReader.getLogItemByUser(userID);
        if (logs != null)
            cacheHistory.put(userID, logs);
        return logs;
    }

    public long getNumUserId() {
//        return userHistories.keySet();
        return cacheHistory.size();
//        return cacheHistory.asMap().keySet();

    }

    public List<HistoryCell> getLogHistory(String userID) {
//        if (!userHistories.containsKey(userID)) {
//            System.out.println("No contain user: " + userID);
//            loadUserHistory(userID);
//        }
//        return userHistories.get(userID);
        List<HistoryCell> result = cacheHistory.getIfPresent(userID);
        if (result == null) {
            System.out.println("No contain user: " + userID);
            loadUserHistory(userID);
        }
        return result;
    }

    public String getRandomUserId() {
        random = (int) (Math.random() * uniqueUserId.size());
        String uid = uniqueUserId.get(random);
        return uid;
    }

    @Override
    public void reload() {
        LOGGER.info("Start reload user history");
        List<String> removeUser = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -10);
        LOGGER.info("Reload history for existed user");
//        for (String userId : userHistories.keySet()) {
        for (Map.Entry<String, List<HistoryCell>> entry : cacheHistory.asMap().entrySet()) {
            loadUserHistory(entry.getKey());
//            Date lastDate = userLastLogTime.get(userId);
//            if (lastDate != null && lastDate.compareTo(calendar.getTime()) < 0) {
//                removeUser.add(userId);
//            }
        }
//        LOGGER.info("Remove old user");
//        for (String uid : removeUser) {
//            userHistories.remove(uid);
//            userLastLogTime.remove(uid);
//        }
        LOGGER.info("Finish reload user history");
    }

    public void startAutoReload() {
        ReloadProcess reloadProcess = new ReloadProcess(this, RELOAD_CIRCLE_TIME);
        reloadProcess.start();
    }
}
