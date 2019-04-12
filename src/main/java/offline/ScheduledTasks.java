package offline;

import config.Resources;


public class ScheduledTasks {
    public static void main(String[] args) {
        new Thread(() -> {
            System.out.println("Starting Training Dictionary thread");
            TrainingDictionary trainingDictionary = new TrainingDictionary(Resources.getInstance().getProperty("data.dictionary_folder"));
            trainingDictionary.start();
        }).start();

        new Thread(() -> {
            System.out.println("Starting Backup Log thread");
            BackupLogToDB backupLogToDB = new BackupLogToDB();
            backupLogToDB.start();
        }).start();

        new Thread(() -> {
            System.out.println("Starting Tokenizer thread");
            SaveTokenizeDb.listening();
        }).start();
    }
}
