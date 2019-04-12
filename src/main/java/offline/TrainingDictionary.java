package offline;

import datastruct.Item;
import io.sql.ItemDBHandler;
import nlp.Dictionary;

import java.io.*;
import java.util.*;

/**
 * Training Dictionary
 * Việc training Dictionary được thực hiện mỗi ngày 1 lần vào đầu ngày, khoảng 0h sáng
 */
public class TrainingDictionary {
    private String dictionPath; // Path của file chứa từ điển
    private String lastIdPath;  // Path của file đánh dấu đã tính tới item nào rồi, hỗ trợ cho việc update từ điển từ những item mới thay vì phải train lại với tất cả item
    private long lastId = 0;    // id của item cuối cùng được update vào từ điển
    private Dictionary dictionary;  // từ điển

    public TrainingDictionary(String folder){
        dictionPath = folder + "/dictionary.txt";
        lastIdPath = folder + "/lastId.txt";
        File file = new File(folder);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdir();
            dictionary = new Dictionary();
        } else {
            BufferedReader bufferedReader = null;
            FileReader fileReader = null;
            try {
                fileReader = new FileReader(lastIdPath);
                bufferedReader = new BufferedReader(fileReader);
                lastId = Long.parseLong(bufferedReader.readLine().trim());
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (lastId == 0) {
                dictionary = new Dictionary();
            } else {
                dictionary = Dictionary.loadFromFile(dictionPath);
                if (dictionary == null) {
                    dictionary = new Dictionary();
                    lastId = 0L;
                }
            }
        }
    }

    private void proccess(){
        System.out.println("Append dictionary");
        ItemDBHandler itemDBHandler = new ItemDBHandler();
        List<Item> items = itemDBHandler.getAllItemFromId(lastId);
        List<List<String>> docs = new ArrayList<>();
        for (Item item : items) {
            docs.add(Arrays.asList(item.getContent().getValue().split("\\s+")));
        }
        System.out.println(docs.size());
        if(items.size() == 0){
            return;
        }
        lastId = items.get(items.size() - 1).getId();
        dictionary.addDocs(docs);
        dictionary.saveToFile(dictionPath);
        BufferedWriter bufferedWriter = null;
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(lastIdPath);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(lastId + "");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (fileWriter != null) {
                fileWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        while(true){
            if(new Date().compareTo(calendar.getTime()) > 0){
                proccess();
                calendar.add(Calendar.DATE, 1);
            }
            try {
                System.out.println("Sleep 1h");
                Thread.sleep(3600000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
