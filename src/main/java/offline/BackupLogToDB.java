package offline;

import config.Resources;
import io.LogObject;
import io.file.FileLogAccess;
import io.sql.UserLogDBHandler;
import org.apache.log4j.Logger;
import utils.MyParser;

import java.io.File;
import java.util.Calendar;
import java.util.List;

/**
 * Backup user log from file to database
 */
public class BackupLogToDB {
    private static final Logger LOGGER = Logger.getLogger(BackupLogToDB.class);
    private static final int BATCH_SIZE = 100;  // Số lượng file log mỗi lần xử lý
    private static final int TIME_RELOAD = 300000;       // Thời gian dãn cách mỗi lần xử lý dữ liệu
    private String logFolder;           // Thư mục chứa log
    private String processedFolder;     // Thư mục chứa log đã xử lý

    public BackupLogToDB() {
        logFolder = Resources.getInstance().getProperty("data.user_log_folder");
        processedFolder = Resources.getInstance().getProperty("data.user_log_processed_folder");
        if (logFolder == null || processedFolder == null) {
            LOGGER.error("log folder is null!!!\nExisted!");
            System.exit(1);
        }
        if (!processedFolder.endsWith("/")) {
            processedFolder += "/";
        }
        File processed = new File(processedFolder);
        if (!processed.exists()) {
            processed.mkdir();
        }
    }

    private int process() {
        LOGGER.info("Reload user history");
        List<File> logFiles = FileLogAccess.getNewFiles(logFolder, BATCH_SIZE);
        int count = 0;
        for (File logFile : logFiles) {
            List<LogObject> logs = FileLogAccess.readLogsFile(logFile);
            if (logs == null) {
                LOGGER.info("Read log file: " + logFile.getPath() + " error");
                continue;
            }
            UserLogDBHandler userLogDBHanlder = new UserLogDBHandler();
            boolean success = userLogDBHanlder.insertLog(logs);
            if (success) {
                logFile.renameTo(new File(processedFolder + logFile.getName()));
            }
            count += logs.size();
            LOGGER.debug("Finish process with file: " + logFile.getPath());
        }
        return count;
    }

    public void start() {
        Integer numDateKeepLog = MyParser.parseInteger(Resources.getInstance().getProperty("log.num_date_keep"));
        if (numDateKeepLog == null)
            numDateKeepLog = 60;
        while (true) {
            int n = process();
            System.out.println("Num row have been inserted: " + n);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -numDateKeepLog);
            int nDelete = UserLogDBHandler.deleteOldLogs(calendar.getTime());
            System.out.println("Num row have been removed: " + nDelete);
            if (n == 0) {
                try {
                    Thread.sleep(TIME_RELOAD);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
}
