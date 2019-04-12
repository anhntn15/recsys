package config;

import java.io.*;
import java.util.Properties;

public class Resources {
    private static volatile Resources instance;
    private Properties pros;
    private final String path = "config/resources.properties";

    private Resources() {
        loadConfig();
    }

    public static Resources getInstance() {
        if (instance == null) {
            synchronized (Resources.class) {
                if (instance == null)
                    instance = new Resources();
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

    public String getProperty(String key) {
        return pros.getProperty(key);
    }

    public void setProperty(String key, String value) {
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
}
