package nlp;

import config.Resources;
import multithread.ReloadProcess;
import multithread.Reloadable;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * Quản lý việc reload Dictionary từ file.
 */
public class DictionaryManager implements Reloadable {
    private static final Logger LOGGER = Logger.getLogger(DictionaryManager.class);
    private String dataFile;
    private long lastModified = 0;
    private Dictionary dictionary;
    private static DictionaryManager instance = null;

    private DictionaryManager(String dataFile) {
        this.dataFile = dataFile;
        System.out.println("dataFile: " + dataFile);
    }

    public static void init() {
        instance = new DictionaryManager(Resources.getInstance().getProperty("data.dictionary"));
        instance.startAutoReload();
    }

    public static DictionaryManager getInstance() {
        return instance;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    /**
     * Khi file dictionary có update thì load lại dictionary từ file
     */
    @Override
    public void reload() {
        File file = new File(dataFile);
        long l = file.lastModified();
        if (l > lastModified) {
            LOGGER.info("Reload Dictionary");
            Dictionary d = Dictionary.loadFromFile(dataFile);
            if (d != null) {
                dictionary = d;
                lastModified = l;
            }
        }
    }

    public void startAutoReload() {
        ReloadProcess reloadProcess = new ReloadProcess(this, 10000L);
        reloadProcess.start();
    }
}
