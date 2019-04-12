package config;

import java.io.*;
import java.util.Properties;

public class BackgroudService {
    private static volatile BackgroudService instance;
    private Properties pros;
    private final String path = "config/background_service.properties";

    private BackgroudService() {
        loadConfig();
    }

    public static BackgroudService getInstance() {
        if (instance == null) {
            synchronized (BackgroudService.class) {
                if (instance == null)
                    instance = new BackgroudService();
            }
        }
        return instance;
    }

    private void loadConfig() {
        pros = new Properties();
        InputStream is = null;
        try {
            is = new FileInputStream(path);
            pros.load(is);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getProperty(String key) {
        return pros.getProperty(key);
    }

    private void setProperty(String key, String value) {
        pros.setProperty(key, value);

        OutputStream os = null;
        try {
            os = new FileOutputStream(path);
            pros.store(os, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getLastPretokenizedID() {
        String id = getProperty("pretokenized_id");
        if (id == null || id.length() == 0)
            id = "0";
        return id;
    }

    public void setLastPretokenizedID(String id) {
        setProperty("pretokenized_id", id);
    }

}
