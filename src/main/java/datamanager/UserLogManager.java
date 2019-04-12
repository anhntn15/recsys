package datamanager;

import config.Resources;
import datastruct.HistoryCell;
import datastruct.Item;
import io.LogObject;
import io.sql.UserLogDBHandler;
import multithread.ReloadProcess;
import multithread.Reloadable;
import org.apache.log4j.Logger;
import utils.MyParser;

import java.util.*;

public class UserLogManager implements Reloadable {
    private static final Logger LOGGER = Logger.getLogger(UserLogManager.class);
    private static final long TIME_RELOAD = 60 * 1000L; // Thời gian dãn cách mỗi lần load dữ liệu
    private static final int LIMIT_GET_LOG = 100000;    // Giới hạn số lượng log tối đa trong mỗi lần query db
    private int numDateKeepLog;         // giới hạn số ngày được log được sử dụng
    private int numLogPerUser;          // giới hạn số lượng log cho mỗi user
    private String lastId = "";         // id của log cuối cùng được load lên

    private static UserLogManager instance = new UserLogManager();
    private static int random;          // Dùng để lấy 1 user ngẫu nhiên
    private Map<String, List<LogObject>> userHistory;   // Chứa list các log tương ứng cho từng user
    private List<String> listUid;       // List các user id

    private UserLogManager() {
        userHistory = new HashMap<>();
        listUid = new ArrayList<>();
        Integer numDateKeepLog = MyParser.parseInteger(Resources.getInstance().getProperty("log.num_date_keep"));
        if (numDateKeepLog == null)
            this.numDateKeepLog = 60;
        else
            this.numDateKeepLog = numDateKeepLog;
        Integer numLogPerUser = MyParser.parseInteger(Resources.getInstance().getProperty("log.num_log_per_user"));
        if (numLogPerUser == null)
            this.numLogPerUser = 60;
        else
            this.numLogPerUser = numLogPerUser;

    }

    public static UserLogManager getInstance() {
        return instance;
    }

    public Set<String> getListUserId() {
        return userHistory.keySet();
    }

    public List<HistoryCell> getLogHistory(String userID) {
        List<LogObject> history = userHistory.get(userID);
        if (history == null) {
            System.out.println("User " + userID + " has empty log");
            return new ArrayList<>();
        } else {
            ItemManager itemManager = ItemManager.getInstance();
            List<HistoryCell> result = new ArrayList<>();
//            for (LogObject logObject : history) {
            int start = history.size() - numLogPerUser;
            if(start < 0){
                start = 0;
            }
            for (int i = start; i < history.size(); i++) {
                LogObject logObject;
                try {
                    logObject = history.get(i);
                }catch (IndexOutOfBoundsException e){
                    continue;
                }
                Item item = itemManager.getItem(logObject.getItemId());
                if (item == null) {
                    continue;
                }
                HistoryCell historyCell = new HistoryCell(item, logObject.getTimeOnSite(), logObject.isClickToCall());
                result.add(historyCell);
            }
            return result;
        }
    }

    public String getRandomUserId() {
        random = (int) (Math.random() * listUid.size());
        return listUid.get(random);
    }

    /**
     *
     */
    @Override
    public void reload() {
        LOGGER.info("Start reload user history");
        System.out.println("Start reload user history. Last id: " + lastId);
        List<LogObject> logObjects = UserLogDBHandler.getNewLog(lastId, LIMIT_GET_LOG);
        if (logObjects == null || logObjects.size() == 0) {
            return;
        }
        for (LogObject logObject : logObjects) {
            String uid = logObject.getUid();
            List<LogObject> history = userHistory.computeIfAbsent(uid, k -> new ArrayList<>());
            history.add(logObject);
        }
        lastId = logObjects.get(logObjects.size() - 1).getId();
        listUid = new ArrayList<>(userHistory.keySet());

        LOGGER.info("Remove old user");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -this.numDateKeepLog);
        HashMap<String, List<LogObject>> newUserHistory = new HashMap<>();
        for (String uid : userHistory.keySet()) {
            List<LogObject> history = userHistory.get(uid);
            List<LogObject> newHistory;
            int it = 0;
            while (history.get(it).getCreateTime().compareTo(calendar.getTime()) < 0) {
                it++;
            }
            newHistory = history.subList(it, history.size());
            if (newHistory.size() > 0) {
                newUserHistory.put(uid, newHistory);
            }
        }
        userHistory = newUserHistory;
        listUid = new ArrayList<>(userHistory.keySet());
        LOGGER.info("Finish reload user history. Num log gotten: " + logObjects.size());
        System.out.println("Finish reload user history. Num log gotten: " + logObjects.size());
    }


    public void startAutoReload() {
        ReloadProcess reloadProcess = new ReloadProcess(this, TIME_RELOAD);
        reloadProcess.start();
    }
}
