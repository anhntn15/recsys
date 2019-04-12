package io.file;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import io.LogObject;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Contain methods access with user log file
 */
public class FileLogAccess {
    private static final int TYPE_PAGEVIEW = 1;     // int
    private static final int TYPE_TIME_ON_SITE = 2;     // int
    private static final int TYPE_CLICK_TO_CALL = 3;     // int

    private static final int USER_ID = 0;       // String
    private static final int PRODUCT_ID = 1;    // int
    private static final int TYPE = 2;     // int
    private static final int TIME_ON_PAGE = 3;     // int
    private static final int CREATE_TIME = 4;     // String

    public static List<File> getNewFiles(String folder, String lastFile) {
        File dataFolder = new File(folder);
        File[] files = dataFolder.listFiles();
        if (files == null) {
            return new ArrayList<>();
        }
        List<File> fileList = new ArrayList<>();
        for (File file : files) {
            String fileName = file.getName();
            if (fileName.length() > 20 && fileName.endsWith(".txt") && fileName.compareTo(lastFile) > 0) {
                fileList.add(file);
            }
        }
        fileList.sort(Comparator.comparing(File::getName));
        return fileList;
    }

    public static List<File> getNewFiles(String folder, int limit) {
        File dataFolder = new File(folder);
        File[] files = dataFolder.listFiles();
        if (files == null) {
            return new ArrayList<>();
        }
        List<File> fileList = new ArrayList<>();
        for (File file : files) {
            String fileName = file.getName();
            if (fileName.length() > 20 && fileName.endsWith(".txt")) {
                fileList.add(file);
                limit--;
                if(limit==0){
                    break;
                }
            }
        }
        fileList.sort(Comparator.comparing(File::getName));
        return fileList;
    }

    public static List<LogObject> readLogsFile(File filePath) {
        System.out.println("Read file: " + filePath.getPath());
        CSVReader reader = null;
        List<LogObject> logs = new ArrayList<>();
        Map<String, List<LogObject>> logGroupByUser = new HashMap<>();
        try {
            reader = new CSVReader(new FileReader(filePath), 1, new CSVParser(';'));
            String[] line;
            int it = 0;

            String name = filePath.getName().split("\\.")[0];
            while ((line = reader.readNext()) != null) {
                Integer type = Integer.parseInt(line[TYPE]);
                String userId = line[USER_ID];
                Long productId = Long.parseLong(line[PRODUCT_ID]);
                Date createDate = parseDateCreate(line[CREATE_TIME]);
                if (type == TYPE_PAGEVIEW) {
                    LogObject item = new LogObject(name + "_" + it, userId, productId, createDate);
                    List<LogObject> ls = logGroupByUser.computeIfAbsent(userId, k -> new ArrayList<>());
                    ls.add(item);
                    logs.add(item);
                } else if (type == TYPE_TIME_ON_SITE) {
                    List<LogObject> ls = logGroupByUser.get(userId);
                    if (ls != null) {
                        for (int i = ls.size() - 1; i >= 0; i--) {
                            if (ls.get(i).getItemId().equals(productId)) {
                                ls.get(i).setTimeOnSite(Integer.parseInt(line[TIME_ON_PAGE]));
                                break;
                            }
                        }
                    }
                } else if (type == TYPE_CLICK_TO_CALL) {
                    List<LogObject> ls = logGroupByUser.get(userId);
                    if (ls != null) {
                        for (int i = ls.size() - 1; i >= 0; i--) {
                            if (ls.get(i).getItemId().equals(productId)) {
                                ls.get(i).setClickToCall(1);
                                break;
                            }
                        }
                    }
                }

                it++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            logs = null;
        }
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logs;
    }

    @Deprecated
    private static Date parseDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        try {
            return format.parse(date.substring(0, 17));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Date parseDateCreate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try {
            return format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
