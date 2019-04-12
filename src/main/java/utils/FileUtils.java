package utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    public static synchronized List<String> readFileByLine(String path) {
        BufferedReader reader = null;
        List<String> list = new ArrayList<>();
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "utf-8"));

            String line;
            while ((line = reader.readLine()) != null)
                list.add(line);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    public static synchronized String readFileFirstLine(String path) {
        BufferedReader reader = null;
        List<String> list = new ArrayList<>();
        String line = null;
        try {
            reader = new BufferedReader(new FileReader(path));
            line = reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return line;
    }

    public static synchronized boolean saveStringToFile(String path, String s) {
        BufferedWriter writer = null;
        boolean done = false;
        try {
            writer = new BufferedWriter(new FileWriter(path));
            writer.write(s);
            done = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return done;
    }
}
